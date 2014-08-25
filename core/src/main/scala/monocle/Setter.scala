package monocle

import scalaz.Functor

final class Setter[S, T, A, B](_modifyF: (A => B) => (S => T)) {

  def modifyF(f: A => B): S => T = _modifyF(f)
  def modify(s: S, f: A => B): T = modifyF(f)(s)

  def set(s: S, newValue: B): T = setF(newValue)(s)
  def setF(newValue: B): S => T = modifyF(_ => newValue)

  // Compose
  def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] =
    Setter[S, T, C, D](_modifyF compose other.modifyF)
  def composeTraversal[C, D](other: Traversal[A, B, C, D]): Setter[S, T, C, D] = composeSetter(other.asSetter)
  def composeOptional[C, D](other: Optional[A, B, C, D]): Setter[S, T, C, D] = composeSetter(other.asSetter)
  def composePrism[C, D](other: Prism[A, B, C, D]): Setter[S, T, C, D] = composeSetter(other.asSetter)
  def composeLens[C, D](other: Lens[A, B, C, D]): Setter[S, T, C, D] = composeSetter(other.asSetter)
  def composeIso[C, D](other: Iso[A, B, C, D]): Setter[S, T, C, D] = composeSetter(other.asSetter)
}

object Setter {

  def apply[S, T, A, B](_modifyF: (A => B) => (S => T)): Setter[S, T, A, B] =
    new Setter[S, T, A, B](_modifyF)

  def apply[F[_]: Functor, A, B]: Setter[F[A], F[B], A, B] =
    new Setter[F[A], F[B], A, B](f => Functor[F].map(_)(f))

}
