package monocle.syntax

import monocle.{ Getter, Traversal, Lens }
import scalaz.Functor

private[syntax] trait LensSyntax {
  implicit def toLensOps[S, T, A, B](lens:  Lens[S, T, A, B]): LensOps[S, T, A, B] = new LensOps(lens)

  implicit def toPartialApplyLensOps[S](value: S): PartialApplyLensOps[S] = new PartialApplyLensOps(value)
}

private[syntax] final class LensOps[S, T, A, B](self: Lens[S, T, A, B]) {
  def |->[C, D](other: Lens[A, B, C, D]): Lens[S, T, C, D] = self compose other
}

private[syntax] trait PartialApplyLens[S, T, A, B] extends PartialApplyTraversal[S, T, A, B] with PartialApplyGetter[S, A] { self =>
  def _lens: Lens[S, T, A, B]

  def _traversal: Traversal[S, T, A, B] = _lens
  def _getter: Getter[S, A] = _lens

  def lift[F[_]: Functor](f: A => F[B]): F[T] = _lens.lift[F](from, f)

  def composeLens[C, D](other: Lens[A, B, C, D]): PartialApplyLens[S, T, C, D] = new PartialApplyLens[S, T, C, D] {
    val from: S = self.from
    val _lens: Lens[S, T, C, D] = self._lens compose other
  }

  /** Alias to composeLens */
  def |->[C, D](other: Lens[A, B, C, D]): PartialApplyLens[S, T, C, D] = composeLens(other)
}

private[syntax] final class PartialApplyLensOps[S](value: S) {
  def partialApplyLens[T, A, B](lens: Lens[S, T, A, B]): PartialApplyLens[S, T, A, B] = new PartialApplyLens[S, T, A, B] {
    val from: S = value
    def _lens: Lens[S, T, A, B] = lens
  }

  /** Alias to partialApplyLens */
  def |->[T, A, B](lens: Lens[S, T, A, B]): PartialApplyLens[S, T, A, B] = partialApplyLens(lens)
}