package monocle.function

import monocle.Iso

import newts._

trait WrappedFunctions {
  def wrapped[S](implicit ev: Newtype[S]): Iso[S, ev.A] =
    Iso[S, ev.A](ev.unwrap(_))(ev.wrap)

  def unwrapped[S](implicit ev: Newtype[S]): Iso[ev.A, S] = wrapped.reverse
}

object Wrapped extends WrappedFunctions
