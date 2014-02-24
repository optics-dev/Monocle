package monocle

import org.scalacheck.Prop._
import org.scalacheck.{Properties, Arbitrary}
import scalaz.{Equal, Functor}

trait Iso[S, T, A, B] extends Lens[S, T, A, B] with Prism[S, T, A, B]{

  def inverse: Iso[B, A, T, S]

  def re: Getter[B, T] = inverse.asGetter

}

object Iso {

  def apply[S, T, A, B](_get: S => A, _reverseGet: B => T): Iso[S, T, A, B] = new Iso[S, T, A, B] { self =>
    def inverse: Iso[B, A, T, S] = new Iso[B, A, T, S] {
      def inverse: Iso[S, T, A, B] = self

      def lift[F[_] : Functor](from: B, f: T => F[S]): F[A] =
        Functor[F].map(f(_reverseGet(from)))(_get)
    }

    def lift[F[_] : Functor](from: S, f: A => F[B]): F[T] =
      Functor[F].map(f(_get(from)))(_reverseGet)
  }

  def laws[S: Arbitrary : Equal, A : Arbitrary : Equal](iso: SimpleIso[S, A]) = new Properties("Iso") {
    include(Lens.laws(iso))
    include(Prism.laws(iso))

    property("double inverse") = forAll { (from: S, newValue: A) =>
      Equal[A].equal(iso.inverse.inverse.get(from), iso.get(from))
      Equal[S].equal(iso.inverse.inverse.set(from, newValue), iso.set(from, newValue))
    }

  }

}
