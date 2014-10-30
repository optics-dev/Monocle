package monocle

import scalaz.Monoid

final case class Getter[S, A](get: S => A) {

  // Compose
  @inline def composeFold[B](other: Fold[A, B]): Fold[S, B] = asFold composeFold other
  @inline def composeGetter[B](other: Getter[A, B]): Getter[S, B] =
    Getter(other.get compose get)
  @inline def composeLens[B, C, D](other: Lens[A, B, C, D]): Getter[S, C] = composeGetter(other.asGetter)
  @inline def composeIso[B, C, D](other: Iso[A, B, C, D]): Getter[S, C] = composeGetter(other.asGetter)

  // Optics transformation
  def asFold: Fold[S, A] = new Fold[S, A]{
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M = f(get(s))
  }

}