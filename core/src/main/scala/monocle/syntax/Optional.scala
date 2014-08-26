package monocle.syntax

import monocle._

import scalaz.Maybe

object optional extends OptionalSyntax

private[syntax] trait OptionalSyntax {
  implicit def toApplyOptionalOps[S](value: S): ApplyOptionalOps[S] = new ApplyOptionalOps(value)
}

final case class ApplyOptionalOps[S](s: S) {
  def applyOptional[T, A, B](optional: Optional[S, T, A, B]): ApplyOptional[S, T, A, B] = ApplyOptional[S, T, A, B](s, optional)
}

final case class ApplyOptional[S, T, A, B](s: S, optional: Optional[S, T, A, B]){
  def getMaybe: Maybe[A] = optional.getMaybe(s)

  def modify(f: A => B): T = optional.modify(s, f)
  def modifyMaybe(f: A => B): Maybe[T] = optional.modifyMaybe(s, f)

  def set(b: B): T = optional.set(s, b)
  def setMaybe(b: B): Maybe[T] = optional.setMaybe(s, b)

  def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, optional composeSetter other)
  def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = ApplyFold(s, optional composeFold other)
  def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, optional composeTraversal other)
  def composeOptional[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, optional composeOptional  other)
  def composePrism[C, D](other: Prism[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, optional composePrism  other)
  def composeLens[C, D](other: Lens[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, optional composeLens other)
  def composeIso[C, D](other: Iso[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, optional composeIso other)
}