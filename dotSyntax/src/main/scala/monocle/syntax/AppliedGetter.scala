package monocle.syntax

import monocle.Getter
import monocle.function._

trait AppliedGetter[A, B] {
  def value: A
  def optic: Getter[A, B]

  def get: B =
    optic.get(value)

  def compose[C](other: Getter[B, C]): AppliedGetter[A, C] =
    AppliedGetter(value, optic.compose(other))

  def _1(implicit ev: Field1[B]): AppliedGetter[A, ev.B] = first
  def _2(implicit ev: Field2[B]): AppliedGetter[A, ev.B] = second
  def _3(implicit ev: Field3[B]): AppliedGetter[A, ev.B] = third
  def _4(implicit ev: Field4[B]): AppliedGetter[A, ev.B] = fourth
  def _5(implicit ev: Field5[B]): AppliedGetter[A, ev.B] = fifth
  def _6(implicit ev: Field6[B]): AppliedGetter[A, ev.B] = sixth

  def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedGetter[A, Option[C]] =
    compose(ev.at(i))

  def first(implicit ev: Field1[B]): AppliedGetter[A, ev.B] =
    compose(ev.first)

  def second(implicit ev: Field2[B]): AppliedGetter[A, ev.B] =
    compose(ev.second)

  def third(implicit ev: Field3[B]): AppliedGetter[A, ev.B] =
    compose(ev.third)

  def fourth(implicit ev: Field4[B]): AppliedGetter[A, ev.B] =
    compose(ev.fourth)

  def fifth(implicit ev: Field5[B]): AppliedGetter[A, ev.B] =
    compose(ev.fifth)

  def sixth(implicit ev: Field6[B]): AppliedGetter[A, ev.B] =
    compose(ev.sixth)

  def reverse(implicit ev: Reverse[B]): AppliedGetter[A, ev.B] =
    compose(ev.reverse)

  def asTarget[C](implicit ev: B =:= C): AppliedGetter[A, C] =
    AppliedGetter(value, optic.asTarget[C])
}

object AppliedGetter {
  def apply[A, B](_value: A, _optic: Getter[A, B]): AppliedGetter[A, B] =
    new AppliedGetter[A, B] {
      def value: A            = _value
      def optic: Getter[A, B] = _optic
    }
}
