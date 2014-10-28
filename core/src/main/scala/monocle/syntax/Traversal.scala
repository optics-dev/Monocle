package monocle.syntax

import monocle._

import scalaz.IList

object traversal extends TraversalSyntax

private[syntax] trait TraversalSyntax {
  implicit def toApplyTraversalOps[S](value: S): ApplyTraversalOps[S] = new ApplyTraversalOps(value)
}

final case class ApplyTraversalOps[S](s: S) {
  def applyTraversal[T, A, B](traversal: Traversal[S, T, A, B]): ApplyTraversal[S, T, A, B] = ApplyTraversal[S, T, A, B](s, traversal)
}

final case class ApplyTraversal[S, T, A, B](s: S, traversal: Traversal[S, T, A, B]){
  def getAll: IList[A] = traversal.getAll(s)
  def set(b: B): T = traversal.set(b)(s)
  def modify(f: A => B): T = traversal.modify(f)(s)

  def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, traversal composeSetter other)
  def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = ApplyFold(s, traversal composeFold other)
  def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, traversal composeTraversal other)
  def composeOptional[C, D](other: Optional[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, traversal composeOptional other)
  def composePrism[C, D](other: Prism[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, traversal composePrism  other)
  def composeLens[C, D](other: PLens[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, traversal composeLens other)
  def composeIso[C, D](other: Iso[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, traversal composeIso other)
}