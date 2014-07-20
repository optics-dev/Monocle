package monocle.syntax

import monocle.{Setter, Fold, Traversal}
import scalaz.Applicative

object traversal extends TraversalSyntax

private[syntax] trait TraversalSyntax {
  implicit def toTraversalOps[S, T, A, B](traversal: Traversal[S, T, A, B]): TraversalOps[S, T, A, B] = new TraversalOps(traversal)

  implicit def toApplyTraversalOps[S](value: S): ApplyTraversalOps[S] = new ApplyTraversalOps(value)
}

private[syntax] final class TraversalOps[S, T, A, B](val self: Traversal[S, T, A, B]) {
  def |->>[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = self compose other
}

private[syntax] trait ApplyTraversal[S, T, A, B] extends ApplySetter[S, T, A, B] with ApplyFold[S, A] { self =>
  def _traversal: Traversal[S, T, A, B]

  def _fold: Fold[S, A] = _traversal
  def _setter: Setter[S, T, A, B] = _traversal

  def multiLift[F[_]: Applicative](f: A => F[B]): F[T] = _traversal.multiLift[F](from, f)

  def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = new ApplyTraversal[S, T, C, D] {
    val from: S = self.from
    val _traversal: Traversal[S, T, C, D] = self._traversal compose other
  }

  /** Alias to composeTraversal */
  def |->>[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = composeTraversal(other)

}

private[syntax] final class ApplyTraversalOps[S](value: S) {
  def applyTraversal[T, A, B](traversal: Traversal[S, T, A, B]): ApplyTraversal[S, T, A, B] = new ApplyTraversal[S, T, A, B] {
    val from: S = value
    def _traversal: Traversal[S, T, A, B] = traversal
  }

  /** Alias to ApplyTraversal */
  def ^|->>[T, A, B](traversal: Traversal[S, T, A, B]): ApplyTraversal[S, T, A, B] = applyTraversal(traversal)
}