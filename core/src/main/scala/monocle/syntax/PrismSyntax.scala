package monocle.syntax

import monocle.{Prism, Traversal}

trait PrismSyntax {

  implicit def toPrismOps[S, T, A, B](prism: Prism[S, T, A, B]): PrismOps[S, T, A, B] = new PrismOps(prism)

  implicit def toAppliedPrismOps[S](value: S): AppliedPrismOps[S] = new AppliedPrismOps(value)

}

final class PrismOps[S, T, A, B](val self: Prism[S, T, A, B]) {
  def <-?[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = self compose other
}

trait AppliedPrism[S, T, A, B] extends AppliedTraversal[S, T, A, B]  { self =>

  def _prism: Prism[S, T, A, B]

  def _traversal: Traversal[S, T, A, B] = _prism

  def getOption: Option[A] = _prism.getOption(from)

  def composePrism[C, D](other: Prism[A, B, C, D]): AppliedPrism[S, T, C, D] = new AppliedPrism[S, T, C, D] {
    val _prism: Prism[S, T, C, D] = self._prism compose other
    val from: S = self.from
  }

  /** Alias to composePrism */
  def <-?[C, D](other: Prism[A, B, C, D]): AppliedPrism[S, T, C, D] = composePrism(other)
}

final class AppliedPrismOps[S](value: S) {
  def <-?[T, A, B](prism: Prism[S, T, A, B]): AppliedPrism[S, T, A, B] = new AppliedPrism[S, T, A, B] {
    val from: S = value
    val _prism: Prism[S, T, A, B] = prism
  }
}
