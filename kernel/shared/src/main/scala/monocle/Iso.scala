package monocle

import monocle.function.Reverse

object Iso {
  def apply[S, A](_get: S => A)(_reverseGet: A => S): Iso[S, A] =
    PIso[S, S, A, A](_get)(_reverseGet)

  def reverse[S, A](implicit ev: Reverse.Aux[S, A]): Iso[S, A] =
    ev.reverse

  def id[S]: Iso[S, S] =
    Iso[S, S](identity)(identity)
}
