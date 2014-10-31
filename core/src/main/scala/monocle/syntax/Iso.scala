package monocle.syntax

import monocle._

object iso extends IsoSyntax

private[syntax] trait IsoSyntax {
  implicit def toApplyIsoOps[S](value: S): ApplyIsoOps[S] = new ApplyIsoOps(value)
}

final case class ApplyIsoOps[S](s: S) {
  @inline def applyIso[T, A, B](iso: Iso[S, T, A, B]): ApplyIso[S, T, A, B] = ApplyIso[S, T, A, B](s, iso)

  /** alias to applyIso */
  @inline def &<->[T, A, B](iso: Iso[S, T, A, B]): ApplyIso[S, T, A, B] = applyIso(iso)
}

final case class ApplyIso[S, T, A, B](s: S, iso: Iso[S, T, A, B]) {
  @inline def get: A = iso.get(s)
  @inline def set(b: B): T = iso.set(b)(s)
  @inline def modify(f: A => B): T = iso.modify(f)(s)

  @inline def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, iso composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = ApplyFold(s, iso composeFold other)
  @inline def composeGetter[C](other: Getter[A, C]): ApplyGetter[S, C] = ApplyGetter(s, iso composeGetter other)
  @inline def composeTraversal[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, iso composeTraversal other)
  @inline def composeOptional[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, iso composeOptional  other)
  @inline def composePrism[C, D](other: Prism[A, B, C, D]): ApplyPrism[S, T, C, D] = ApplyPrism(s, iso composePrism  other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] = ApplyLens(s, iso composeLens other)
  @inline def composeIso[C, D](other: Iso[A, B, C, D]): ApplyIso[S, T, C, D] = ApplyIso(s, iso composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: Traversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = composeTraversal(other)
  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = composeOptional(other)
  /** alias to composePrism */
  @inline def ^<-?[C, D](other: Prism[A, B, C, D]): ApplyPrism[S, T, C, D] = composePrism(other)
  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] = composeLens(other)
  /** alias to composeIso */
  @inline def ^<->[C, D](other: Iso[A, B, C, D]): ApplyIso[S, T, C, D] = composeIso(other)
}


