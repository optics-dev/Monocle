package monocle

import monocle.util.Contravariant
import monocle.util.Contravariant.coerce
import org.scalacheck.Prop._
import org.scalacheck.{Properties, Arbitrary}
import scalaz.Id._
import scalaz.std.list._
import scalaz.{Traverse, Applicative, Equal}


trait Traversal[S, T, A, B] extends Setter[S, T, A, B] with Fold[S, A] { self =>

  def multiLift[F[_] : Applicative](from: S, f: A => F[B]):  F[T]

  def modify(from: S, f: A => B): T = multiLift[Id](from, { a: A => id.point(f(a)) } )

  protected def underlyingFold[F[_] : Contravariant : Applicative](from: S)(f: A => F[A]): F[S] =
    coerce[F, T, S](multiLift(from, { a: A => coerce[F, A, B](f(a)) }))

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
      Equal[S].equal(traversal.multiLift[Id](from, id.point[A](_)), from)
    }

    property("set - get all") = forAll { (from: S, newValue: A) =>
      Equal[List[A]].equal(traversal.toListOf(traversal.set(from, newValue)), traversal.toListOf(from) map (_ => newValue))
    }
  }
}
