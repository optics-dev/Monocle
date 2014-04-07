package monocle.syntax

import monocle.{Traversal, Prism, Lens, Iso}

trait IsoSyntax {

  implicit def toIsoOps[S, T, A, B](iso:  Iso[S, T, A, B]): IsoOps[S, T, A, B] = new IsoOps(iso)

  implicit def toAppliedIsoOps[S](value: S): AppliedIsoOps[S] = new AppliedIsoOps(value)
  
}

final class IsoOps[S, T, A, B](val self: Iso[S, T, A, B]) {
  def <->[C, D](other: Iso[A, B, C, D]): Iso[S, T, C, D] = self compose other
}

trait AppliedIso[S, T, A, B] extends AppliedLens[S, T, A, B] with AppliedPrism[S, T, A, B] { self =>

  def _iso: Iso[S, T, A, B]

  override val _traversal: Traversal[S, T, A, B] = _iso

  def _lens: Lens[S, T, A, B] = _iso
  def _prism: Prism[S, T, A, B] = _iso


  def <->[C, D](other: Iso[A, B, C, D]): AppliedIso[S, T, C, D] = new AppliedIso[S, T, C, D] {
    val from: S = self.from
    val _iso: Iso[S, T, C, D] = self._iso compose other
  }
}

class AppliedIsoOps[S](value: S) {
  def <->[T, A, B](iso: Iso[S, T, A, B]): AppliedIso[S, T, A, B] = new AppliedIso[S, T, A, B] {
    val from: S = value
    def _iso: Iso[S, T, A, B] = iso
  }
}