package monocle.syntax

import monocle.Prism
import monocle.function.Cons

trait AppliedPrism[A, B] extends AppliedOptional[A, B] {
  def value: A
  def optic: Prism[A, B]

  def compose[C](other: Prism[B, C]): AppliedPrism[A, C] =
    AppliedPrism(value, optic.compose(other))

  override def cons(implicit ev: Cons[B]): AppliedPrism[A, (ev.A, B)] =
    compose(ev.cons)
}

object AppliedPrism {
  def apply[A, B](_value: A, _optic: Prism[A, B]): AppliedPrism[A, B] =
    new AppliedPrism[A, B] {
      def value: A = _value
      def optic: Prism[A, B] = _optic
    }
}