package monocle.applied

import monocle.Lens
import monocle.function._

trait AppliedLens[A, B] extends AppliedOptional[A, B] with AppliedGetter[A, B] {
  def value: A
  def optic: Lens[A, B]

  def compose[C](other: Lens[B, C]): AppliedLens[A, C] =
    AppliedLens(value, optic.compose(other))

  override def _1(implicit ev: Field1[B]): AppliedLens[A, ev.B] = first
  override def _2(implicit ev: Field2[B]): AppliedLens[A, ev.B] = second
  override def _3(implicit ev: Field3[B]): AppliedLens[A, ev.B] = third
  override def _4(implicit ev: Field4[B]): AppliedLens[A, ev.B] = fourth
  override def _5(implicit ev: Field5[B]): AppliedLens[A, ev.B] = fifth
  override def _6(implicit ev: Field6[B]): AppliedLens[A, ev.B] = sixth

  override def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedLens[A, Option[C]] =
    compose(ev.at(i))

  override def first(implicit ev: Field1[B]): AppliedLens[A, ev.B] =
    compose(ev.first)

  override def second(implicit ev: Field2[B]): AppliedLens[A, ev.B] =
    compose(ev.second)

  override def third(implicit ev: Field3[B]): AppliedLens[A, ev.B] =
    compose(ev.third)

  override def fourth(implicit ev: Field4[B]): AppliedLens[A, ev.B] =
    compose(ev.fourth)

  override def fifth(implicit ev: Field5[B]): AppliedLens[A, ev.B] =
    compose(ev.fifth)

  override def sixth(implicit ev: Field6[B]): AppliedLens[A, ev.B] =
    compose(ev.sixth)

  override def reverse(implicit ev: Reverse[B]): AppliedLens[A, ev.B] =
    compose(ev.reverse)

  override def asTarget[C](implicit ev: B =:= C): AppliedLens[A, C] =
    AppliedLens(value, optic.asTarget[C])
}

object AppliedLens {
  def apply[A, B](_value: A, _optic: Lens[A, B]): AppliedLens[A, B] =
    new AppliedLens[A, B] {
      def value: A          = _value
      def optic: Lens[A, B] = _optic
    }
}
