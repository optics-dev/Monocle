package monocle

import monocle.util.Constant
import monocle.util.Constant._
import org.scalacheck.Prop._
import org.scalacheck.{ Properties, Arbitrary }
import scalaz.Id._
import scalaz.std.list._
import scalaz.{ Monoid, Traverse, Applicative, Equal }

/**
 * A Traversal is generalisation of a Lens in a way that it defines a multi foci between
 * S and 0 to many A.
 */
trait Traversal[S, T, A, B] extends Setter[S, T, A, B] with Fold[S, A] { self =>

  def multiLift[F[_]: Applicative](from: S, f: A => F[B]): F[T]

  def modify(from: S, f: A => B): T = multiLift[Id](from, { a: A => id.point(f(a)) })

  def foldMap[M: Monoid](from: S)(f: A => M): M =
    multiLift[({ type l[a] = Constant[M, a] })#l](from, { a: A => Constant[M, B](f(a)) })

  def asTraversal: Traversal[S, T, A, B] = self

  def compose[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = new Traversal[S, T, C, D] {
    def multiLift[F[_]: Applicative](from: S, f: C => F[D]): F[T] = self.multiLift(from, other.multiLift(_, f))
  }

}

object Traversal {

  def apply[T[_]: Traverse, A, B]: Traversal[T[A], T[B], A, B] = new Traversal[T[A], T[B], A, B] {
    def multiLift[F[_]: Applicative](from: T[A], f: A => F[B]): F[T[B]] = Traverse[T].traverse(from)(f)
  }

  def make2[S, T, A, B](get1: S => A)(get2: S => A)(_set: (S, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def multiLift[F[_]: Applicative](from: S, f: A => F[B]): F[T] =
      Applicative[F].apply2(f(get1(from)), f(get2(from)))((v1, v2) => _set(from, v1, v2))
  }

  def laws[S: Arbitrary: Equal, A: Arbitrary: Equal](traversal: SimpleTraversal[S, A]) = new Properties("Traversal") {
    import scalaz.syntax.equal._
    include(Setter.laws(traversal))

    property("multi lift - identity") = forAll { from: S =>
      traversal.multiLift[Id](from, id.point[A](_)) === from
    }

    property("set - get all") = forAll { (from: S, newValue: A) =>
      traversal.toListOf(traversal.set(from, newValue)) === traversal.toListOf(from).map(_ => newValue)
    }
  }
}
