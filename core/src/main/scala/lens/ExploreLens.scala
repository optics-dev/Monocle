package lens

import scalaz.{Applicative, Functor}
import lens.util.Identity


trait Setter3[S, T, A, B] {
  protected def setter(lift: A => Identity[B])(from: S): Identity[T]
  def modify(from: S, f: A => B): T = setter{a : A => Identity(f(a))}(from).value
}

trait Traversal3[S, T, A, B] extends Setter3[S, T, A, B] {
  protected def traversal[F[_]: Applicative](lift: A => F[B])(from: S): F[T]
  protected def setter(lift: A => Identity[B])(from: S): Identity[T] = traversal[Identity](lift)(from)
}

trait Lens3[S, T, A, B] extends Traversal3[S, T, A, B] {
  protected def lens[F[_]: Functor](lift: A => F[B])(from: S): F[T]
  protected def traversal[F[_] : Applicative](lift: A => F[B])(from: S): F[T] = lens(lift)(from)
}

object Lens3 {
  def apply[S, T, A, B](_get: S => A, _set: (S, B) => T): Lens3[S, T, A, B] = new Lens3[S, T, A, B] {
    def lens[F[_]: Functor](lift: A => F[B])(from: S): F[T] =
      Functor[F].map(lift(_get(from))){newValue: B => _set(from, newValue) }
  }
}


trait Setter2[S, T, A, B] extends ((A => Identity[B]) => S => Identity[T])

// Traversal2 should inherit Setter2 if it was defined for all Functors

trait Traversal2[F[_], S, T, A, B] extends ((Applicative[F]) => (A => F[B]) => S => F[T])

trait Lens2[F[_], S, T, A, B] extends ((Functor[F]) => (A => F[B]) => S => F[T]) with Traversal2[F, S, T, A, B]


object Lens2 {

  def apply[F[_], S, T, A, B](_get: S => A, _set: (S, B) => T): Lens2[F, S, T, A, B] = new Lens2[F, S, T, A, B] {
    def apply(functor: Functor[F]): (A => F[B]) => S => F[T] = { lift: (A => F[B]) => { from: S =>
      functor.map(lift(_get(from))){newValue: B => _set(from, newValue) }
    }}

  }

}


object Test extends App {

  def modify[S, T, A, B](traversal: Traversal2[Identity, S, T, A, B]): (S, A => B) => T = { case (s, f) =>
    traversal(Applicative[Identity]){ a: A => Identity(f(a))}(s).value
  }

  type SLens[F[_], S, A] = Lens2[F, S, S, A, A]

  case class Person(_name: String, _age: Int)

  val p = Person("Roger", 23)

  val age2 = Lens2[Identity, Person, Person, Int, Int](_._age, { case (from, newAge) => from.copy(_age = newAge)})
  val age3 = Lens3[Person, Person, Int, Int](_._age, { case (from, newAge) => from.copy(_age = newAge)})

  case class Point(x: Int, y: Int)

  val pointTraversal = new Traversal2[Identity, Point, Point, Int, Int] {
    def apply(applicative: Applicative[Identity]): (Int => Identity[Int]) => Point => Identity[Point] =
    { lift: (Int => Identity[Int]) => { from: Point =>
      import scalaz.syntax.applicative._
      implicit val ev: Applicative[Identity] = applicative
      (lift(from.x) |@| lift(from.y))((newX, newY) =>
        from.copy(x = newX, y = newY)
      )
    }}

  }

  println(modify(age2)(p, _ + 2))
  println(age3.modify(p, _ + 2))

}
