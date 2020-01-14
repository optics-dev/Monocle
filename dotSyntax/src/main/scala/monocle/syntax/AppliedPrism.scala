package monocle.syntax

import monocle.Prism

trait AppliedPrism[From, To] extends AppliedOptional[From, To] {
  def value: From
  def optic: Prism[From, To]

  def andThen[X](other: Prism[To, X]): AppliedPrism[From, X] =
    AppliedPrism(value, optic.andThen(other))

  override def asTarget[X](implicit ev: To =:= X): AppliedPrism[From, X] =
    AppliedPrism(value, optic.asTarget[X])

  override def some[X](implicit ev: To =:= Option[X]): AppliedPrism[From, X] =
    asTarget[Option[X]].andThen(Prism.some[X])
}

object AppliedPrism {
  def apply[From, To](_value: From, _optic: Prism[From, To]): AppliedPrism[From, To] =
    new AppliedPrism[From, To] {
      def value: From            = _value
      def optic: Prism[From, To] = _optic
    }
}
