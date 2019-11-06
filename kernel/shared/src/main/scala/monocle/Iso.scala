package monocle

import monocle.function.Reverse

object Iso {
  
  def apply[S, A](_get: S => A)(_reverseGet: A => S): Iso[S, A] =
    new Iso[S, A] {
      def get(from: S): A      = _get(from)
      def reverseGet(to: A): S = _reverseGet(to)
    }

  def reverse[S, A](implicit ev: Reverse.Aux[S, A]): Iso[S, A] =
    ev.reverse

  def id[S]: Iso[S, S] =
    Iso[S, S](identity)(identity)
}
