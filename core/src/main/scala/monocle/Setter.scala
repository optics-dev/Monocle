package monocle

import scalaz.Functor

final case class Setter[S, T, A, B](modify: (A => B) => (S => T)) {

  def set(b: B): S => T = modify(_ => b)

  // Compose
  def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] =
    Setter[S, T, C, D](modify compose other.modify)
  def composeTraversal[C, D](other: Traversal[A, B, C, D]): Setter[S, T, C, D] = composeSetter(other.asSetter)
  def composeOptional[C, D](other: Optional[A, B, C, D]): Setter[S, T, C, D] = composeSetter(other.asSetter)
  def composePrism[C, D](other: Prism[A, B, C, D]): Setter[S, T, C, D] = composeSetter(other.asSetter)
  def composeLens[C, D](other: Lens[A, B, C, D]): Setter[S, T, C, D] = composeSetter(other.asSetter)
  def composeIso[C, D](other: Iso[A, B, C, D]): Setter[S, T, C, D] = composeSetter(other.asSetter)
}

object Setter {

  def apply[F[_]: Functor, A, B]: Setter[F[A], F[B], A, B] =
    new Setter[F[A], F[B], A, B](f => Functor[F].map(_)(f))

}
