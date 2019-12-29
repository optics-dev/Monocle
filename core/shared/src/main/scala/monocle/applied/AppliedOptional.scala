package monocle.applied

import monocle.{Optional, Prism}
import monocle.function._

trait AppliedOptional[A, B] extends AppliedFold[A, B] with AppliedSetter[A, B] {
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

  override def asTarget[C](implicit ev: B =:= C): AppliedOptional[A, C] =
    AppliedOptional(value, optic.asTarget[C])

  ///////////////////////////////////
  // dot syntax for optics typeclass
  ///////////////////////////////////

  override def _1(implicit ev: Field1[B]): AppliedOptional[A, ev.B] = first(ev)
  override def _2(implicit ev: Field2[B]): AppliedOptional[A, ev.B] = second(ev)
  override def _3(implicit ev: Field3[B]): AppliedOptional[A, ev.B] = third(ev)
  override def _4(implicit ev: Field4[B]): AppliedOptional[A, ev.B] = fourth(ev)
  override def _5(implicit ev: Field5[B]): AppliedOptional[A, ev.B] = fifth(ev)
  override def _6(implicit ev: Field6[B]): AppliedOptional[A, ev.B] = sixth(ev)

  override def first(implicit ev: Field1[B]): AppliedOptional[A, ev.B]  = compose(ev.first)
  override def second(implicit ev: Field2[B]): AppliedOptional[A, ev.B] = compose(ev.second)
  override def third(implicit ev: Field3[B]): AppliedOptional[A, ev.B]  = compose(ev.third)
  override def fourth(implicit ev: Field4[B]): AppliedOptional[A, ev.B] = compose(ev.fourth)
  override def fifth(implicit ev: Field5[B]): AppliedOptional[A, ev.B]  = compose(ev.fifth)
  override def sixth(implicit ev: Field6[B]): AppliedOptional[A, ev.B]  = compose(ev.sixth)

  override def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedOptional[A, Option[C]] = compose(ev.at(i))
  override def cons(implicit ev: Cons[B]): AppliedOptional[A, (ev.B, B)]                   = compose(ev.cons)
  override def headOption(implicit ev: Cons[B]): AppliedOptional[A, ev.B]                  = compose(ev.headOption)
  override def tailOption(implicit ev: Cons[B]): AppliedOptional[A, B]                     = compose(ev.tailOption)
  override def index[I, C](i: I)(implicit ev: Index.Aux[B, I, C]): AppliedOptional[A, C]   = compose(ev.index(i))
  override def possible(implicit ev: Possible[B]): AppliedOptional[A, ev.B]                = compose(ev.possible)
  override def reverse(implicit ev: Reverse[B]): AppliedOptional[A, ev.B]                  = compose(ev.reverse)

  ///////////////////////////////////
  // dot syntax for standard types
  ///////////////////////////////////

  override def left[E, C](implicit ev: B =:= Either[E, C]): AppliedOptional[A, E] =
    asTarget[Either[E, C]].compose(Prism.left[E, C])
  override def right[E, C](implicit ev: B =:= Either[E, C]): AppliedOptional[A, C] =
    asTarget[Either[E, C]].compose(Prism.right[E, C])
  override def some[C](implicit ev: B =:= Option[C]): AppliedOptional[A, C] = asTarget[Option[C]].compose(Prism.some[C])
}

object AppliedOptional {
  def apply[A, B](_value: A, _optic: Optional[A, B]): AppliedOptional[A, B] =
    new AppliedOptional[A, B] {
      def value: A              = _value
      def optic: Optional[A, B] = _optic
    }
}
