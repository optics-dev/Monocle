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
  @inline def set(b: B): T = setter.set(b)(s)
  @inline def modify(f: A => B): T = setter.modify(f)(s)

  @inline def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composeSetter other)
  @inline def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composeTraversal other)
  @inline def composeOptional[C, D](other: Optional[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composeOptional other)
  @inline def composePrism[C, D](other: Prism[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composePrism  other)
  @inline def composeLens[C, D](other: Lens[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composeLens other)
  @inline def composeIso[C, D](other: Iso[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, setter composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: Traversal[A, B, C, D]): ApplySetter[S, T, C, D] = composeTraversal(other)
  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: Optional[A, B, C, D]): ApplySetter[S, T, C, D] = composeOptional(other)
  /** alias to composePrism */
  @inline def ^<-?[C, D](other: Prism[A, B, C, D]): ApplySetter[S, T, C, D] = composePrism(other)
  /** alias to composeLens */
  @inline def ^|->[C, D](other: Lens[A, B, C, D]): ApplySetter[S, T, C, D] = composeLens(other)
  /** alias to composeIso */
  @inline def ^<->[C, D](other: Iso[A, B, C, D]): ApplySetter[S, T, C, D] = composeIso(other)
}