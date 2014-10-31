package monocle

import scalaz.Functor

final case class PSetter[S, T, A, B](modify: (A => B) => (S => T)) {

  @inline def set(b: B): S => T = modify(_ => b)

  // Compose
  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    PSetter[S, T, C, D](modify compose other.modify)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PSetter[S, T, C, D] = composeSetter(other.asSetter)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): PSetter[S, T, C, D] = composeSetter(other.asSetter)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): PSetter[S, T, C, D] = composeSetter(other.asSetter)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): PSetter[S, T, C, D] = composeSetter(other.asSetter)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): PSetter[S, T, C, D] = composeSetter(other.asSetter)
}

object PSetter {
  def apply[F[_]: Functor, A, B]: PSetter[F[A], F[B], A, B] =
    new PSetter[F[A], F[B], A, B](f => Functor[F].map(_)(f))
}

object Setter {
  @inline def apply[S, A](modify: (A => A) => (S => S)): Setter[S, A] =
    PSetter(modify)
}