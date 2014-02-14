package monocle.syntax

import monocle.{Traversal, Fold, Setter}
import scalaz.Applicative


trait AppliedTraversal[S, T, A, B] extends AppliedSetter[S, T, A, B] with AppliedFold[S, A] { self =>

  def _traversal: Traversal[S, T, A, B]

  def _fold: Fold[S, A] = _traversal
  def _setter: Setter[S, T, A, B] = _traversal

  def multiLift[F[_] : Applicative](f: A => F[B]):  F[T] = _traversal.multiLift[F](from, f)


  def oo[C, D](other: Traversal[A, B, C, D]): AppliedTraversal[S, T, C, D] = new AppliedTraversal[S, T, C, D] {
    val from: S = self.from
    val _traversal: Traversal[S, T, C, D] = self._traversal compose other
  }

}

trait ToAppliedTraversalOps {
  implicit class AppliedTraversalOps[S](value: S){
    def >--[T, A, B](traversal: Traversal[S, T, A, B]): AppliedTraversal[S, T, A, B] = new AppliedTraversal[S, T, A, B] {
      val from: S = value
      def _traversal: Traversal[S, T, A, B] = traversal
    }
  }
}