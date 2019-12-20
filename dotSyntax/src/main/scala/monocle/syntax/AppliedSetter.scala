package monocle.syntax

import monocle.Setter
import monocle.function._

trait AppliedSetter[A, B] {
  def value: A
  def optic: Setter[A, B]

  def set: B => A =
    optic.set(_)(value)

  def compose[C](other: Setter[B, C]): AppliedSetter[A, C] =
    AppliedSetter(value, optic.compose(other))

  def _1(implicit ev: Field1[B]): AppliedSetter[A, ev.B] = first
  def _2(implicit ev: Field2[B]): AppliedSetter[A, ev.B] = second
  def _3(implicit ev: Field3[B]): AppliedSetter[A, ev.B] = third
  def _4(implicit ev: Field4[B]): AppliedSetter[A, ev.B] = fourth
  def _5(implicit ev: Field5[B]): AppliedSetter[A, ev.B] = fifth
  def _6(implicit ev: Field6[B]): AppliedSetter[A, ev.B] = sixth

  def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedSetter[A, Option[C]] =
    compose(ev.at(i))

  def first(implicit ev: Field1[B]): AppliedSetter[A, ev.B] =
    compose(ev.first)

  def second(implicit ev: Field2[B]): AppliedSetter[A, ev.B] =
    compose(ev.second)

  def third(implicit ev: Field3[B]): AppliedSetter[A, ev.B] =
    compose(ev.third)

  def fourth(implicit ev: Field4[B]): AppliedSetter[A, ev.B] =
    compose(ev.fourth)

  def fifth(implicit ev: Field5[B]): AppliedSetter[A, ev.B] =
    compose(ev.fifth)

  def sixth(implicit ev: Field6[B]): AppliedSetter[A, ev.B] =
    compose(ev.sixth)

  def asTarget[C](implicit ev: B =:= C): AppliedSetter[A, C] =
    AppliedSetter(value, optic.asTarget[C])
}

object AppliedSetter {
  def apply[A, B](_value: A, _optic: Setter[A, B]): AppliedSetter[A, B] =
    new AppliedSetter[A, B] {
      def value: A            = _value
      def optic: Setter[A, B] = _optic
    }
}
