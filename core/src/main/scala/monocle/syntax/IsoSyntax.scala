package monocle.syntax

import monocle.{Traversal, Prism, Lens, Iso}

private[syntax] trait IsoSyntax {

  implicit def toIsoOps[S, T, A, B](iso:  Iso[S, T, A, B]): IsoOps[S, T, A, B] = new IsoOps(iso)

  implicit def toPartialApplyIsoOps[S](value: S): PartialApplyIsoOps[S] = new PartialApplyIsoOps(value)
  
}

private[syntax] final class IsoOps[S, T, A, B](val self: Iso[S, T, A, B]) {
  def <->[C, D](other: Iso[A, B, C, D]): Iso[S, T, C, D] = self compose other
}

private[syntax] trait PartialApplyIso[S, T, A, B] extends PartialApplyLens[S, T, A, B] with PartialApplyPrism[S, T, A, B] { self =>
  def _iso: Iso[S, T, A, B]

  override val _traversal: Traversal[S, T, A, B] = _iso

  def _lens: Lens[S, T, A, B] = _iso
  def _prism: Prism[S, T, A, B] = _iso

  def composeIso[C, D](other: Iso[A, B, C, D]): PartialApplyIso[S, T, C, D] = new PartialApplyIso[S, T, C, D] {
    val from: S = self.from
    def _iso: Iso[S, T, C, D] = self._iso compose other
  }

  /** Alias to composeIso */
  def <->[C, D](other: Iso[A, B, C, D]): PartialApplyIso[S, T, C, D] = composeIso(other)
}

private[syntax] final class PartialApplyIsoOps[S](value: S) {
  def partialApplyIso[T, A, B](iso: Iso[S, T, A, B]): PartialApplyIso[S, T, A, B] = new PartialApplyIso[S, T, A, B] {
    val from: S = value
    def _iso: Iso[S, T, A, B] = iso
  }

  /** Alias to partialApplyIso */
  def <->[T, A, B](iso: Iso[S, T, A, B]): PartialApplyIso[S, T, A, B] = partialApplyIso(iso)
}
