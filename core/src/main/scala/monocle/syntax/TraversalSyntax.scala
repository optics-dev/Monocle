package monocle.syntax

import monocle.{Setter, Fold, Traversal}
import scalaz.Applicative

trait TraversalSyntax {

  implicit def toTraversalOps[S, T, A, B](traversal: Traversal[S, T, A, B]): TraversalOps[S, T, A, B] = new TraversalOps(traversal)

  implicit def toAppliedTraversalOps[S](value: S): AppliedTraversalOps[S] = new AppliedTraversalOps(value)

}

final class TraversalOps[S, T, A, B](val self: Traversal[S, T, A, B]) {
  def |->>[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = self compose other
}

trait AppliedTraversal[S, T, A, B] extends AppliedSetter[S, T, A, B] with AppliedFold[S, A] { self =>

  def _traversal: Traversal[S, T, A, B]

  def _fold: Fold[S, A] = _traversal
  def _setter: Setter[S, T, A, B] = _traversal

  def multiLift[F[_]: Applicative](f: A => F[B]): F[T] = _traversal.multiLift[F](from, f)

  def |->>[C, D](other: Traversal[A, B, C, D]): AppliedTraversal[S, T, C, D] = new AppliedTraversal[S, T, C, D] {
    val from: S = self.from
    val _traversal: Traversal[S, T, C, D] = self._traversal compose other
  }

}

final class AppliedTraversalOps[S](value: S) {
  def |->>[T, A, B](traversal: Traversal[S, T, A, B]): AppliedTraversal[S, T, A, B] = new AppliedTraversal[S, T, A, B] {
    val from: S = value
    def _traversal: Traversal[S, T, A, B] = traversal
  }
}