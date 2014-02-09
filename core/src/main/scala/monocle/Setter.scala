package monocle

import org.scalacheck.Prop.forAll
import org.scalacheck.{Properties, Arbitrary}
import scalaz.{Functor, Equal}


trait Setter[S, T, A, B] { self =>

  def set(from: S, newValue: B): T = modify(from, _ => newValue)

  def modify(from: S, f: A => B): T

  def asSetter: Setter[S, T, A, B] = self

    def compose[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = new Setter[S, T, C, D] {
    def modify(from: S, f: C => D): T = self.modify(from, other.modify(_, f))
  }

}

object Setter {

  def apply[F[_]: Functor, A, B]: Setter[F[A], F[B], A, B] = new Setter[F[A], F[B], A, B] {
    def modify(from: F[A], f: A => B): F[B] = Functor[F].map(from)(f)
  }

  def laws[S: Arbitrary : Equal, A : Arbitrary](setter: Setter[S, S, A, A]) = new Properties("Setter") {
    property("modify - identity") = forAll { from: S =>
      Equal[S].equal(setter.modify(from, identity), from)
    }

    property("set - set") = forAll { (from: S, newValue: A) =>
      val setOnce = setter.set(from, newValue)
      Equal[S].equal(setOnce, setter.set(setOnce, newValue))
    }
  }



}
