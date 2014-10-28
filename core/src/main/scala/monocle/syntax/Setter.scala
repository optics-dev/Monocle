package monocle.syntax

import monocle._

object setter extends SetterSyntax

private[syntax] trait SetterSyntax {
  implicit def toApplySetterOps[S](value: S): ApplySetterOps[S] = new ApplySetterOps(value)
}

final case class ApplySetterOps[S](s: S) {
  def applySetter[T, A, B](setter: Setter[S, T, A, B]): ApplySetter[S, T, A, B] = new ApplySetter[S, T, A, B](s, setter)
}

final case class ApplySetter[S, T, A, B](s: S, setter: Setter[S, T, A, B]) {
  def set(b: B): T = setter.set(b)(s)
  def modify(f: A => B): T = setter.modify(f)(s)

  def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composeSetter other)
  def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composeTraversal other)
  def composeOptional[C, D](other: Optional[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composeOptional other)
  def composePrism[C, D](other: Prism[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composePrism  other)
  def composeLens[C, D](other: PLens[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composeLens other)
  def composeIso[C, D](other: Iso[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composeIso other)
}