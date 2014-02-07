package monocle.util

import scalaz.Functor


trait Contravariant[F[_]] {
  def contramap[A, B](f: A => B)(fb: F[B]): F[A]
}

object Contravariant {
  def apply[F[_]](implicit ev: Contravariant[F]): Contravariant[F] = ev

  // todo: Review, functor is unused
  def coerce[F[_] : Contravariant : Functor , A, B](fa: F[A]): F[B] =
    Contravariant[F].contramap[B, A]{_: B => ??? }(fa)

}
