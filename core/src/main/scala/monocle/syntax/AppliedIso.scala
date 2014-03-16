package monocle.syntax

import monocle.{Traversal, Prism, Lens, Iso}

trait AppliedIso[S, T, A, B] extends AppliedLens[S, T, A, B] with AppliedPrism[S, T, A, B] { self =>

  def _iso: Iso[S, T, A, B]

  override val _traversal: Traversal[S, T, A, B] = _iso

  def _lens: Lens[S, T, A, B] = _iso
  def _prism: Prism[S, T, A, B] = _iso


  def oo[C, D](other: Iso[A, B, C, D]): AppliedIso[S, T, C, D] = new AppliedIso[S, T, C, D] {
    val from: S = self.from
    val _iso: Iso[S, T, C, D] = self._iso compose other
  }
}

trait ToAppliedIsoOps {
  implicit class AppliedIsoOps[S](value: S) {
    def >-[T, A, B](iso: Iso[S, T, A, B]): AppliedIso[S, T, A, B] = new AppliedIso[S, T, A, B] {
      val from: S = value
      def _iso: Iso[S, T, A, B] = iso
    }
  }
}