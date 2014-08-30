package monocle.syntax

import monocle._

object iso extends IsoSyntax

private[syntax] trait IsoSyntax {
  implicit def toApplyIsoOps[S](value: S): ApplyIsoOps[S] = new ApplyIsoOps(value)
}

final case class ApplyIsoOps[S](s: S) {
  def applyIso[T, A, B](iso: Iso[S, T, A, B]): ApplyIso[S, T, A, B] = ApplyIso[S, T, A, B](s, iso)
}

final case class ApplyIso[S, T, A, B](s: S, iso: Iso[S, T, A, B]) {
  def get: A = iso.get(s)
  def set(b: B): T = iso.set(b)(s)
  def modify(f: A => B): T = iso.modify(f)(s)

  def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, iso composeSetter other)
  def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = ApplyFold(s, iso composeFold other)
  def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, iso composeTraversal other)
  def composeOptional[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, iso composeOptional  other)
  def composePrism[C, D](other: Prism[A, B, C, D]): ApplyPrism[S, T, C, D] = ApplyPrism(s, iso composePrism  other)
  def composeLens[C, D](other: Lens[A, B, C, D]): ApplyLens[S, T, C, D] = ApplyLens(s, iso composeLens other)
  def composeIso[C, D](other: Iso[A, B, C, D]): ApplyIso[S, T, C, D] = ApplyIso(s, iso composeIso other)
}


