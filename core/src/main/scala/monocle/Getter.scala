package monocle

import scalaz.Monoid

final class Getter[S, A](_get: S => A) {

  def get(s: S): A = _get(s)

  // Compose
  def composeFold[B](other: Fold[A, B]): Fold[S, B] = asFold composeFold other
  def composeGetter[B](other: Getter[A, B]): Getter[S, B] =
    Getter(other.get _ compose get)
  def composeLens[B, C, D](other: Lens[A, B, C, D]): Getter[S, C] = composeGetter(other.asGetter)
  def composeIso[B, C, D](other: Iso[A, B, C, D]): Getter[S, C] = composeGetter(other.asGetter)

  // Optics transformation
  def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[B: Monoid](s: S)(f: A => B): B = f(get(s))
  }

}

object Getter {
  def apply[S, A](_get: S => A): Getter[S, A] = new Getter[S, A](_get)
}
