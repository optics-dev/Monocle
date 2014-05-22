package monocle.syntax

import monocle.{Optional, Prism, Lens, Iso}

private[syntax] trait IsoSyntax {

  implicit def toIsoOps[S, T, A, B](iso:  Iso[S, T, A, B]): IsoOps[S, T, A, B] = new IsoOps(iso)

  implicit def toApplyIsoOps[S](value: S): ApplyIsoOps[S] = new ApplyIsoOps(value)
  
}

private[syntax] final class IsoOps[S, T, A, B](val self: Iso[S, T, A, B]) {
  def <->[C, D](other: Iso[A, B, C, D]): Iso[S, T, C, D] = self compose other
}

private[syntax] trait ApplyIso[S, T, A, B] extends ApplyLens[S, T, A, B] with ApplyPrism[S, T, A, B] { self =>
  def _iso: Iso[S, T, A, B]

  override def _optional: Optional[S, T, A, B] = _iso

  def _lens: Lens[S, T, A, B] = _iso
  def _prism: Prism[S, T, A, B] = _iso

  def composeIso[C, D](other: Iso[A, B, C, D]): ApplyIso[S, T, C, D] = new ApplyIso[S, T, C, D] {
    val from: S = self.from
    def _iso: Iso[S, T, C, D] = self._iso compose other
  }

  /** Alias to composeIso */
  def <->[C, D](other: Iso[A, B, C, D]): ApplyIso[S, T, C, D] = composeIso(other)
}

private[syntax] final class ApplyIsoOps[S](value: S) {
  def applyIso[T, A, B](iso: Iso[S, T, A, B]): ApplyIso[S, T, A, B] = new ApplyIso[S, T, A, B] {
    val from: S = value
    def _iso: Iso[S, T, A, B] = iso
  }

  /** Alias to ApplyIso */
  def <->[T, A, B](iso: Iso[S, T, A, B]): ApplyIso[S, T, A, B] = applyIso(iso)
}
