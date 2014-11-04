package monocle.syntax

import monocle._

import scalaz.Maybe

object optional extends OptionalSyntax

private[syntax] trait OptionalSyntax {
  implicit def toApplyOptionalOps[S](value: S): ApplyOptionalOps[S] = new ApplyOptionalOps(value)
}

final case class ApplyOptionalOps[S](s: S) {
  def applyOptional[T, A, B](optional: Optional[S, T, A, B]): ApplyOptional[S, T, A, B] = ApplyOptional[S, T, A, B](s, optional)
  /** alias to applyOptional */
  def &|-?[T, A, B](optional: Optional[S, T, A, B]): ApplyOptional[S, T, A, B] = applyOptional(optional)
}

final case class ApplyOptional[S, T, A, B](s: S, optional: Optional[S, T, A, B]){
  @inline def getMaybe: Maybe[A] = optional.getMaybe(s)

  @inline def modify(f: A => B): T = optional.modify(f)(s)
  @inline def modifyMaybe(f: A => B): Maybe[T] = optional.modifyMaybe(f)(s)

  @inline def set(b: B): T = optional.set(b)(s)
  @inline def setMaybe(b: B): Maybe[T] = optional.setMaybe(b)(s)

  @inline def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, optional composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = ApplyFold(s, optional composeFold other)
  @inline def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, optional composeTraversal other)
  @inline def composeOptional[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, optional composeOptional  other)
  @inline def composePrism[C, D](other: Prism[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, optional composePrism  other)
  @inline def composeLens[C, D](other: Lens[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, optional composeLens other)
  @inline def composeIso[C, D](other: Iso[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, optional composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = composeTraversal(other)
  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = composeOptional(other)
  /** alias to composePrism */
  @inline def ^<-?[C, D](other: Prism[A, B, C, D]): ApplyOptional[S, T, C, D] = composePrism(other)
  /** alias to composeLens */
  @inline def ^|->[C, D](other: Lens[A, B, C, D]): ApplyOptional[S, T, C, D] = composeLens(other)
  /** alias to composeIso */
  @inline def ^<->[C, D](other: Iso[A, B, C, D]): ApplyOptional[S, T, C, D] = composeIso(other)
}