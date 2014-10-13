package monocle.syntax

import monocle._

import scalaz.IList

object traversal extends TraversalSyntax

private[syntax] trait TraversalSyntax {
  implicit def toApplyTraversalOps[S](value: S): ApplyTraversalOps[S] = new ApplyTraversalOps(value)
}

final case class ApplyTraversalOps[S](s: S) {
  def applyTraversal[T, A, B](traversal: Traversal[S, T, A, B]): ApplyTraversal[S, T, A, B] = ApplyTraversal[S, T, A, B](s, traversal)
  /** alias to applyTraversal */
  def &|->>[T, A, B](traversal: Traversal[S, T, A, B]): ApplyTraversal[S, T, A, B] = applyTraversal(traversal)
}

final case class ApplyTraversal[S, T, A, B](s: S, traversal: Traversal[S, T, A, B]){
  @inline def getAll: IList[A] = traversal.getAll(s)
  @inline def set(b: B): T = traversal.set(b)(s)
  @inline def modify(f: A => B): T = traversal.modify(f)(s)

  @inline def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, traversal composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = ApplyFold(s, traversal composeFold other)
  @inline def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, traversal composeTraversal other)
  @inline def composeOptional[C, D](other: Optional[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, traversal composeOptional other)
  @inline def composePrism[C, D](other: Prism[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, traversal composePrism  other)
  @inline def composeLens[C, D](other: Lens[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, traversal composeLens other)
  @inline def composeIso[C, D](other: Iso[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, traversal composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = composeTraversal(other)
  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: Optional[A, B, C, D]): ApplyTraversal[S, T, C, D] = composeOptional(other)
  /** alias to composePrism */
  @inline def ^<-?[C, D](other: Prism[A, B, C, D]): ApplyTraversal[S, T, C, D] = composePrism(other)
  /** alias to composeLens */
  @inline def ^|->[C, D](other: Lens[A, B, C, D]): ApplyTraversal[S, T, C, D] = composeLens(other)
  /** alias to composeIso */
  @inline def ^<->[C, D](other: Iso[A, B, C, D]): ApplyTraversal[S, T, C, D] = composeIso(other)
}