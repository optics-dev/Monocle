package monocle.applied

import monocle.Getter
import monocle.function._

trait AppliedGetter[A, B] extends AppliedFold[A, B] {
  def value: A
  def optic: Getter[A, B]

  def get: B =
    optic.get(value)

  def compose[C](other: Getter[B, C]): AppliedGetter[A, C] =
    AppliedGetter(value, optic.compose(other))

  override def asTarget[C](implicit ev: B =:= C): AppliedGetter[A, C] =
    AppliedGetter(value, optic.asTarget[C])

  ///////////////////////////////////
  // dot syntax for optics typeclass
  ///////////////////////////////////

  override def _1(implicit ev: Field1[B]): AppliedGetter[A, ev.B] = first(ev)
  override def _2(implicit ev: Field2[B]): AppliedGetter[A, ev.B] = second(ev)
  override def _3(implicit ev: Field3[B]): AppliedGetter[A, ev.B] = third(ev)
  override def _4(implicit ev: Field4[B]): AppliedGetter[A, ev.B] = fourth(ev)
  override def _5(implicit ev: Field5[B]): AppliedGetter[A, ev.B] = fifth(ev)
  override def _6(implicit ev: Field6[B]): AppliedGetter[A, ev.B] = sixth(ev)

  override def first(implicit ev: Field1[B]): AppliedGetter[A, ev.B]  = compose(ev.first)
  override def second(implicit ev: Field2[B]): AppliedGetter[A, ev.B] = compose(ev.second)
  override def third(implicit ev: Field3[B]): AppliedGetter[A, ev.B]  = compose(ev.third)
  override def fourth(implicit ev: Field4[B]): AppliedGetter[A, ev.B] = compose(ev.fourth)
  override def fifth(implicit ev: Field5[B]): AppliedGetter[A, ev.B]  = compose(ev.fifth)
  override def sixth(implicit ev: Field6[B]): AppliedGetter[A, ev.B]  = compose(ev.sixth)

  override def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedGetter[A, Option[C]] = compose(ev.at(i))
  override def reverse(implicit ev: Reverse[B]): AppliedGetter[A, ev.B]                  = compose(ev.reverse)
}

object AppliedGetter {
  def apply[A, B](_value: A, _optic: Getter[A, B]): AppliedGetter[A, B] =
    new AppliedGetter[A, B] {
      def value: A            = _value
      def optic: Getter[A, B] = _optic
    }
}
