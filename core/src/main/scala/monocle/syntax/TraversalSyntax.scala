package monocle.syntax

import monocle.{Setter, Fold, Traversal}
import scalaz.Applicative

private[syntax] trait TraversalSyntax {
  implicit def toTraversalOps[S, T, A, B](traversal: Traversal[S, T, A, B]): TraversalOps[S, T, A, B] = new TraversalOps(traversal)

  implicit def toPartialApplyTraversalOps[S](value: S): PartialApplyTraversalOps[S] = new PartialApplyTraversalOps(value)
}

private[syntax] final class TraversalOps[S, T, A, B](val self: Traversal[S, T, A, B]) {
  def |->>[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = self compose other
}

private[syntax] trait PartialApplyTraversal[S, T, A, B] extends PartialApplySetter[S, T, A, B] with PartialApplyFold[S, A] { self =>
  def _traversal: Traversal[S, T, A, B]

  def _fold: Fold[S, A] = _traversal
  def _setter: Setter[S, T, A, B] = _traversal

  def multiLift[F[_]: Applicative](f: A => F[B]): F[T] = _traversal.multiLift[F](from, f)

  def composeTraversal[C, D](other: Traversal[A, B, C, D]): PartialApplyTraversal[S, T, C, D] = new PartialApplyTraversal[S, T, C, D] {
    val from: S = self.from
    val _traversal: Traversal[S, T, C, D] = self._traversal compose other
  }

  /** Alias to composeTraversal */
  def |->>[C, D](other: Traversal[A, B, C, D]): PartialApplyTraversal[S, T, C, D] = composeTraversal(other)

}

private[syntax] final class PartialApplyTraversalOps[S](value: S) {
  def partialApplyTraversal[T, A, B](traversal: Traversal[S, T, A, B]): PartialApplyTraversal[S, T, A, B] = new PartialApplyTraversal[S, T, A, B] {
    val from: S = value
    def _traversal: Traversal[S, T, A, B] = traversal
  }

  /** Alias to partialApplyTraversal */
  def |->>[T, A, B](traversal: Traversal[S, T, A, B]): PartialApplyTraversal[S, T, A, B] = partialApplyTraversal(traversal)
}