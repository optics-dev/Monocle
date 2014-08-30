package monocle.syntax

import monocle._

import scalaz.Maybe

object prism extends PrismSyntax

private[syntax] trait PrismSyntax {
  implicit def toApplyPrismOps[S](value: S): ApplyPrismOps[S] = new ApplyPrismOps(value)
}

final case class ApplyPrismOps[S](s: S) {
  def applyPrism[T, A, B](prism: Prism[S, T, A, B]): ApplyPrism[S, T, A, B] = ApplyPrism[S, T, A, B](s, prism)
}

final case class ApplyPrism[S, T, A, B](s: S, prism: Prism[S, T, A, B]){
  def getMaybe: Maybe[A] = prism.getMaybe(s)

  def modify(f: A => B): T = prism.modify(f)(s)
  def modifyMaybe(f: A => B): Maybe[T] = prism.modifyMaybe(f)(s)

  def set(b: B): T = prism.set(b)(s)
  def setMaybe(b: B): Maybe[T] = prism.setMaybe(b)(s)

  def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, prism composeSetter other)
  def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = ApplyFold(s, prism composeFold other)
  def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, prism composeTraversal other)
  def composeOptional[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, prism composeOptional  other)
  def composeLens[C, D](other: Lens[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, prism composeLens other)
  def composePrism[C, D](other: Prism[A, B, C, D]): ApplyPrism[S, T, C, D] = ApplyPrism(s, prism composePrism  other)
  def composeIso[C, D](other: Iso[A, B, C, D]): ApplyPrism[S, T, C, D] = ApplyPrism(s, prism composeIso other)
}