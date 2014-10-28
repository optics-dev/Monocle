package monocle.syntax

import monocle._

object lens extends LensSyntax

private[syntax] trait LensSyntax {
  implicit def toApplyLensOps[S](value: S): ApplyLensOps[S] = new ApplyLensOps(value)
}

final case class ApplyLensOps[S](s: S) {
  def applyLens[T, A, B](lens: PLens[S, T, A, B]): ApplyLens[S, T, A, B] = ApplyLens[S, T, A, B](s, lens)
}

final case class ApplyLens[S, T, A, B](s: S, lens: PLens[S, T, A, B]){
  def get: A = lens.get(s)
  def set(b: B): T = lens.set(b)(s)
  def modify(f: A => B): T = lens.modify(f)(s)

  def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, lens composeSetter other)
  def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = ApplyFold(s, lens composeFold other)
  def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, lens composeTraversal other)
  def composeOptional[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, lens composeOptional  other)
  def composePrism[C, D](other: Prism[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, lens composePrism  other)
  def composeLens[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] = ApplyLens(s, lens composeLens other)
  def composeIso[C, D](other: Iso[A, B, C, D]): ApplyLens[S, T, C, D] = ApplyLens(s, lens composeIso other)
}