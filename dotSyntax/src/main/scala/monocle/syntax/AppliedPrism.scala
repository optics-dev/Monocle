package monocle.syntax

import monocle.Prism

trait AppliedPrism[A, B] extends AppliedOptional[A, B] {
  def value: A
  def optic: Prism[A, B]

  def compose[C](other: Prism[B, C]): AppliedPrism[A, C] =
    AppliedPrism(value, optic.compose(other))

  override def asTarget[C](implicit ev: B =:= C): AppliedPrism[A, C] =
    AppliedPrism(value, optic.asTarget[C])

  override def some[C](implicit ev: B =:= Option[C]): AppliedPrism[A, C] =
    asTarget[Option[C]].compose(Prism.some[C])
}

object AppliedPrism {
  def apply[A, B](_value: A, _optic: Prism[A, B]): AppliedPrism[A, B] =
    new AppliedPrism[A, B] {
      def value: A           = _value
      def optic: Prism[A, B] = _optic
    }
}
