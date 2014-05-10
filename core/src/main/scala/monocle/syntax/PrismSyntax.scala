package monocle.syntax

import monocle.{Prism, Traversal}

private[syntax] trait PrismSyntax {
  implicit def toPrismOps[S, T, A, B](prism: Prism[S, T, A, B]): PrismOps[S, T, A, B] = new PrismOps(prism)

  implicit def toPartiallyApplyPrismOps[S](value: S): PartiallyApplyPrismOps[S] = new PartiallyApplyPrismOps(value)
}

private[syntax] final class PrismOps[S, T, A, B](val self: Prism[S, T, A, B]) {
  def <-?[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = self compose other
}

private[syntax] trait PartialApplyPrism[S, T, A, B] extends PartialApplyTraversal[S, T, A, B]  { self =>
  def _prism: Prism[S, T, A, B]

  def _traversal: Traversal[S, T, A, B] = _prism

  def getOption: Option[A] = _prism.getOption(from)

  def composePrism[C, D](other: Prism[A, B, C, D]): PartialApplyPrism[S, T, C, D] = new PartialApplyPrism[S, T, C, D] {
    val _prism: Prism[S, T, C, D] = self._prism compose other
    val from: S = self.from
  }

  /** Alias to composePrism */
  def <-?[C, D](other: Prism[A, B, C, D]): PartialApplyPrism[S, T, C, D] = composePrism(other)
}

private[syntax] final class PartiallyApplyPrismOps[S](value: S) {
  def partialApplyPrism[T, A, B](prism: Prism[S, T, A, B]): PartialApplyPrism[S, T, A, B] = new PartialApplyPrism[S, T, A, B] {
    val from: S = value
    def _prism: Prism[S, T, A, B] = prism
  }

  def <-?[T, A, B](prism: Prism[S, T, A, B]): PartialApplyPrism[S, T, A, B] = partialApplyPrism(prism)
}
