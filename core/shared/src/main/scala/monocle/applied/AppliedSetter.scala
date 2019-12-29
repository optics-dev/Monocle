package monocle.applied

import monocle._
import monocle.function._

trait AppliedSetter[A, B] {
  def value: A
  def optic: Setter[A, B]

  def set: B => A =
    optic.set(_)(value)

  def compose[C](other: Setter[B, C]): AppliedSetter[A, C] =
    AppliedSetter(value, optic.compose(other))

  def asTarget[C](implicit ev: B =:= C): AppliedSetter[A, C] =
    AppliedSetter(value, optic.asTarget[C])

  ///////////////////////////////////
  // dot syntax for optics typeclass
  ///////////////////////////////////

  def _1(implicit ev: Field1[B]): AppliedSetter[A, ev.B] = first(ev)
  def _2(implicit ev: Field2[B]): AppliedSetter[A, ev.B] = second(ev)
  def _3(implicit ev: Field3[B]): AppliedSetter[A, ev.B] = third(ev)
  def _4(implicit ev: Field4[B]): AppliedSetter[A, ev.B] = fourth(ev)
  def _5(implicit ev: Field5[B]): AppliedSetter[A, ev.B] = fifth(ev)
  def _6(implicit ev: Field6[B]): AppliedSetter[A, ev.B] = sixth(ev)

  def first(implicit ev: Field1[B]): AppliedSetter[A, ev.B]  = compose(ev.first)
  def second(implicit ev: Field2[B]): AppliedSetter[A, ev.B] = compose(ev.second)
  def third(implicit ev: Field3[B]): AppliedSetter[A, ev.B]  = compose(ev.third)
  def fourth(implicit ev: Field4[B]): AppliedSetter[A, ev.B] = compose(ev.fourth)
  def fifth(implicit ev: Field5[B]): AppliedSetter[A, ev.B]  = compose(ev.fifth)
  def sixth(implicit ev: Field6[B]): AppliedSetter[A, ev.B]  = compose(ev.sixth)

  def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedSetter[A, Option[C]] = compose(ev.at(i))
  def cons(implicit ev: Cons[B]): AppliedSetter[A, (ev.B, B)]                   = compose(ev.cons)
  def headOption(implicit ev: Cons[B]): AppliedSetter[A, ev.B]                  = compose(ev.headOption)
  def tailOption(implicit ev: Cons[B]): AppliedSetter[A, B]                     = compose(ev.tailOption)
  def index[I, C](i: I)(implicit ev: Index.Aux[B, I, C]): AppliedSetter[A, C]   = compose(ev.index(i))
  def possible(implicit ev: Possible[B]): AppliedSetter[A, ev.B]                = compose(ev.possible)
  def reverse(implicit ev: Reverse[B]): AppliedSetter[A, ev.B]                  = compose(ev.reverse)

  ///////////////////////////////////
  // dot syntax for standard types
  ///////////////////////////////////

  def left[E, C](implicit ev: B =:= Either[E, C]): AppliedSetter[A, E] =
    asTarget[Either[E, C]].compose(Prism.left[E, C])
  def right[E, C](implicit ev: B =:= Either[E, C]): AppliedSetter[A, C] =
    asTarget[Either[E, C]].compose(Prism.right[E, C])
  def some[C](implicit ev: B =:= Option[C]): AppliedSetter[A, C] = asTarget[Option[C]].compose(Prism.some[C])
}

object AppliedSetter {
  def apply[A, B](_value: A, _optic: Setter[A, B]): AppliedSetter[A, B] =
    new AppliedSetter[A, B] {
      def value: A            = _value
      def optic: Setter[A, B] = _optic
    }
}
