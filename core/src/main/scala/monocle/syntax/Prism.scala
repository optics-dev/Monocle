package monocle.syntax

import monocle._

import scalaz.Maybe

object prism extends PrismSyntax

private[syntax] trait PrismSyntax {
  implicit def toApplyPrismOps[S](value: S): ApplyPrismOps[S] = new ApplyPrismOps(value)
}

final case class ApplyPrismOps[S](s: S) {
  def applyPrism[T, A, B](prism: Prism[S, T, A, B]): ApplyPrism[S, T, A, B] = ApplyPrism[S, T, A, B](s, prism)
  /** alias to applyPrism */
  def &<-?[T, A, B](prism: Prism[S, T, A, B]): ApplyPrism[S, T, A, B] = applyPrism(prism)
}

final case class ApplyPrism[S, T, A, B](s: S, prism: Prism[S, T, A, B]){
  @inline def getMaybe: Maybe[A] = prism.getMaybe(s)

  @inline def modify(f: A => B): T = prism.modify(f)(s)
  @inline def modifyMaybe(f: A => B): Maybe[T] = prism.modifyMaybe(f)(s)

  @inline def set(b: B): T = prism.set(b)(s)
  @inline def setMaybe(b: B): Maybe[T] = prism.setMaybe(b)(s)

  @inline def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, prism composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = ApplyFold(s, prism composeFold other)
  @inline def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, prism composeTraversal other)
  @inline def composeOptional[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, prism composeOptional  other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, prism composeLens other)
  @inline def composePrism[C, D](other: Prism[A, B, C, D]): ApplyPrism[S, T, C, D] = ApplyPrism(s, prism composePrism  other)
  @inline def composeIso[C, D](other: Iso[A, B, C, D]): ApplyPrism[S, T, C, D] = ApplyPrism(s, prism composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = composeTraversal(other)
  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = composeOptional(other)
  /** alias to composePrism */
  @inline def ^<-?[C, D](other: Prism[A, B, C, D]): ApplyPrism[S, T, C, D] = composePrism(other)
  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyOptional[S, T, C, D] = composeLens(other)
  /** alias to composeIso */
  @inline def ^<->[C, D](other: Iso[A, B, C, D]): ApplyPrism[S, T, C, D] = composeIso(other)
}