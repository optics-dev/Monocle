package monocle

import monocle.util.{Constant, Identity}
import org.scalacheck.Prop._
import org.scalacheck.{Properties, Arbitrary}
import scalaz._
import scalaz.std.list._


trait Traversal[S, T, A, B] extends Setter[S, T ,A, B] with Fold[S, A] { self =>

  def multiLift[F[_] : Applicative](from: S, f: A => F[B]):  F[T]

  def getAll(from: S): List[A] = {
    val lift: A => Constant[List[A], B] = { a: A => Constant(List(a))}
    multiLift[({type l[a] = Constant[List[A],a]})#l](from,lift).value.reverse
  }

  def modify(from: S, f: A => B): T = multiLift(from, {a: A => Identity(f(a)) }).value

  def fold(from: S)(implicit ev: Monoid[A]): A = Foldable[List].foldMap(getAll(from))(identity)

  def compose[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = new Traversal[S, T, C, D] {
    def multiLift[F[_] : Applicative](from: S, f: C => F[D]): F[T] = self.multiLift(from,  other.multiLift(_, f))
  }

}


object Traversal {

  def apply[T[_]: Traverse, A, B]: Traversal[T[A], T[B], A, B] = new Traversal[T[A], T[B], A, B] {
    def multiLift[F[_] : Applicative](from: T[A], f: A => F[B]): F[T[B]] = Traverse[T].traverse(from)(f)
  }

  def make2[S, T, A, B](get1: S => A)(get2: S => A)(_set: (S, B, B) => T ): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def multiLift[F[_] : Applicative](from: S, f: A => F[B]): F[T] =
      Applicative[F].apply2(f(get1(from)), f(get2(from)))((v1, v2) => _set(from, v1, v2))
  }

  def laws[S : Arbitrary : Equal, A : Arbitrary : Equal](traversal: Traversal[S, S, A, A]) = new Properties("Traversal") {
    include(Setter.laws(traversal))

    property("multi lift - identity") = forAll { from: S =>
      Equal[S].equal(traversal.multiLift[Identity](from, Identity[A]).value, from)
    }

    property("set - get all") = forAll { (from: S, newValue: A) =>
      Equal[List[A]].equal(traversal.getAll(traversal.set(from, newValue)), traversal.getAll(from) map (_ => newValue))
    }
  }

}
