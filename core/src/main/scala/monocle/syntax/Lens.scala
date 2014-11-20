package monocle.syntax

import monocle._

object lens extends LensSyntax

private[syntax] trait LensSyntax {
  implicit def toApplyLensOps[S](value: S): ApplyLensOps[S] = new ApplyLensOps(value)
}

final case class ApplyLensOps[S](s: S) {
  def applyLens[T, A, B](lens: PLens[S, T, A, B]): ApplyLens[S, T, A, B] = ApplyLens[S, T, A, B](s, lens)
  /** alias to applyIso */
  def &|->[T, A, B](lens: PLens[S, T, A, B]): ApplyLens[S, T, A, B] = applyLens(lens)
}

final case class ApplyLens[S, T, A, B](s: S, lens: PLens[S, T, A, B]){
  @inline def get: A = lens.get(s)
  @inline def set(b: B): T = lens.set(b)(s)
  @inline def modify(f: A => B): T = lens.modify(f)(s)

  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] = ApplySetter(s, lens composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = ApplyFold(s, lens composeFold other)
  @inline def composeGetter[C](other: Getter[A, C]): ApplyGetter[S, C] = ApplyGetter(s, lens composeGetter other)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = ApplyTraversal(s, lens composeTraversal other)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, lens composeOptional  other)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] = ApplyOptional(s, lens composePrism  other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] = ApplyLens(s, lens composeLens other)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): ApplyLens[S, T, C, D] = ApplyLens(s, lens composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = composeTraversal(other)
  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] = composeOptional(other)
  /** alias to composePrism */
  @inline def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] = composePrism(other)
  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] = composeLens(other)
  /** alias to composeIso */
  @inline def ^<->[C, D](other: PIso[A, B, C, D]): ApplyLens[S, T, C, D] = composeIso(other)
}