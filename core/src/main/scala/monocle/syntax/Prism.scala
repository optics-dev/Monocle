package monocle.syntax

import monocle.{Prism, SimplePrism, Optional}

object prism extends PrismSyntax

private[syntax] trait PrismSyntax {
  implicit def toPrismOps[S, T, A, B](prism: Prism[S, T, A, B]): PrismOps[S, T, A, B] = new PrismOps(prism)
  implicit def toSimplePrismOps[S, T, A, B](prism: SimplePrism[S, A]): SimplePrismOps[S, A] = new SimplePrismOps(prism)

  implicit def toApplyPrismOps[S](value: S): ApplyPrismOps[S] = new ApplyPrismOps(value)
}

private[syntax] final class PrismOps[S, T, A, B](val self: Prism[S, T, A, B]) {
  def <-?[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = self composePrism other
}

private[syntax] final class SimplePrismOps[S, A](prism: SimplePrism[S, A]){
  def reverseModify(from: A, f: S => S): Option[A] =  prism.getOption(f(prism.reverseGet(from)))
}

private[syntax] trait ApplyPrism[S, T, A, B] extends ApplyOptional[S, T, A, B]  { self =>
  def _prism: Prism[S, T, A, B]

  def _optional: Optional[S, T, A, B] = _prism

  def composePrism[C, D](other: Prism[A, B, C, D]): ApplyPrism[S, T, C, D] = new ApplyPrism[S, T, C, D] {
    val _prism: Prism[S, T, C, D] = self._prism composePrism other
    val from: S = self.from
  }

  /** Alias to composePrism */
  def <-?[C, D](other: Prism[A, B, C, D]): ApplyPrism[S, T, C, D] = composePrism(other)
}

private[syntax] final class ApplyPrismOps[S](value: S) {
  def applyPrism[T, A, B](prism: Prism[S, T, A, B]): ApplyPrism[S, T, A, B] = new ApplyPrism[S, T, A, B] {
    val from: S = value
    def _prism: Prism[S, T, A, B] = prism
  }

  def <-?[T, A, B](prism: Prism[S, T, A, B]): ApplyPrism[S, T, A, B] = applyPrism(prism)
}
