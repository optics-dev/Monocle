package monocle.syntax

import monocle.{Optional, Prism}
import monocle.function._

trait AppliedOptional[A, B] {
  def value: A
  def optic: Optional[A, B]

  def getOption: Option[B] =
    optic.getOption(value)

  def set(to: B): A =
    optic.set(to)(value)

  def modify(f: B => B): A =
    optic.modify(f)(value)

  def compose[C](other: Optional[B, C]): AppliedOptional[A, C] =
    AppliedOptional(value, optic.compose(other))

  def _1(implicit ev: Field1[B]): AppliedOptional[A, ev.B] = first
  def _2(implicit ev: Field2[B]): AppliedOptional[A, ev.B] = second
  def _3(implicit ev: Field3[B]): AppliedOptional[A, ev.B] = third
  def _4(implicit ev: Field4[B]): AppliedOptional[A, ev.B] = fourth
  def _5(implicit ev: Field5[B]): AppliedOptional[A, ev.B] = fifth
  def _6(implicit ev: Field6[B]): AppliedOptional[A, ev.B] = sixth

  def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedOptional[A, Option[C]] =
    compose(ev.at(i))

  def cons(implicit ev: Cons[B]): AppliedOptional[A, (ev.B, B)] =
    compose(ev.cons)

  def first(implicit ev: Field1[B]): AppliedOptional[A, ev.B] =
    compose(ev.first)

  def headOption(implicit ev: Cons[B]): AppliedOptional[A, ev.B] =
    compose(ev.headOption)

  def index[I, C](i: I)(implicit ev: Index.Aux[B, I, C]): AppliedOptional[A, C] =
    compose(ev.index(i))

  def left[E, C](implicit ev: B =:= Either[E, C]): AppliedOptional[A, E] =
    asTarget[Either[E, C]].compose(Prism.left[E, C])

  def right[E, C](implicit ev: B =:= Either[E, C]): AppliedOptional[A, C] =
    asTarget[Either[E, C]].compose(Prism.right[E, C])

  def second(implicit ev: Field2[B]): AppliedOptional[A, ev.B] =
    compose(ev.second)

  def third(implicit ev: Field3[B]): AppliedOptional[A, ev.B] =
    compose(ev.third)

  def fourth(implicit ev: Field4[B]): AppliedOptional[A, ev.B] =
    compose(ev.fourth)

  def fifth(implicit ev: Field5[B]): AppliedOptional[A, ev.B] =
    compose(ev.fifth)

  def sixth(implicit ev: Field6[B]): AppliedOptional[A, ev.B] =
    compose(ev.sixth)

  def reverse(implicit ev: Reverse[B]): AppliedOptional[A, ev.B] =
    compose(ev.reverse)

  def some[C](implicit ev: B =:= Option[C]): AppliedOptional[A, C] =
    asTarget[Option[C]].compose(Prism.some[C])

  def tailOption(implicit ev: Cons[B]): AppliedOptional[A, B] =
    compose(ev.tailOption)

  def asTarget[C](implicit ev: B =:= C): AppliedOptional[A, C] =
    AppliedOptional(value, optic.asTarget[C])
}

object AppliedOptional {
  def apply[A, B](_value: A, _optic: Optional[A, B]): AppliedOptional[A, B] =
    new AppliedOptional[A, B] {
      def value: A = _value
      def optic: Optional[A, B] = _optic
    }
}
