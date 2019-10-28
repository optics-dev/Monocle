package monocle.syntax

import monocle.Prism
import monocle.function._

trait AppliedPrism[A, B] extends AppliedOptional[A, B] {
  def value: A
  def optic: Prism[A, B]

  def compose[C](other: Prism[B, C]): AppliedPrism[A, C] =
    AppliedPrism(value, optic.compose(other))

  override def cons(implicit ev: Cons[B]): AppliedPrism[A, (ev.B, B)] =
    compose(ev.cons)

  override def left[E, C](implicit ev: B =:= Either[E, C]): AppliedPrism[A, E] =
    asTarget[Either[E, C]].compose(Prism.left[E, C])

  override def right[E, C](implicit ev: B =:= Either[E, C]): AppliedPrism[A, C] =
    asTarget[Either[E, C]].compose(Prism.right[E, C])

  override def some[C](implicit ev: B =:= Option[C]): AppliedPrism[A, C] =
    asTarget[Option[C]].compose(Prism.some[C])

  override def reverse(implicit ev: Reverse[B]): AppliedPrism[A, ev.B] =
    compose(ev.reverse)

  override def asTarget[C](implicit ev: B =:= C): AppliedPrism[A, C] =
    AppliedPrism(value, optic.asTarget[C])
}

object AppliedPrism {
  def apply[A, B](_value: A, _optic: Prism[A, B]): AppliedPrism[A, B] =
    new AppliedPrism[A, B] {
      def value: A = _value
      def optic: Prism[A, B] = _optic
    }
}
