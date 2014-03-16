package monocle.syntax

import scalaz.Functor
import monocle.{ Getter, Traversal, Lens }

trait AppliedLens[S, T, A, B] extends AppliedTraversal[S, T, A, B] with AppliedGetter[S, A] { self =>

  def _lens: Lens[S, T, A, B]

  def _traversal: Traversal[S, T, A, B] = _lens
  def _getter: Getter[S, A] = _lens

  def lift[F[_]: Functor](f: A => F[B]): F[T] = _lens.lift[F](from, f)

  def lCompose[C, D](other: Lens[A, B, C, D]): AppliedLens[S, T, C, D] = oo(other)

  def oo[C, D](other: Lens[A, B, C, D]): AppliedLens[S, T, C, D] = new AppliedLens[S, T, C, D] {
    val from: S = self.from
    val _lens: Lens[S, T, C, D] = self._lens compose other
  }
}

trait ToAppliedLensOps {
  implicit class AppliedLensOps[S](value: S) {
    def >-[T, A, B](lens: Lens[S, T, A, B]): AppliedLens[S, T, A, B] = new AppliedLens[S, T, A, B] {
      val from: S = value
      val _lens: Lens[S, T, A, B] = lens
    }
  }
}
