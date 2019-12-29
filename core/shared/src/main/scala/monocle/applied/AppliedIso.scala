package monocle.applied

import monocle.function._
import monocle.{Iso, Prism}

trait AppliedIso[A, B] extends AppliedLens[A, B] with AppliedPrism[A, B] {
  def value: A
  def optic: Iso[A, B]

  final def compose[C](other: Iso[B, C]): AppliedIso[A, C] =
    AppliedIso(value, optic.compose(other))

  override def asTarget[C](implicit ev: B =:= C): AppliedIso[A, C] =
    AppliedIso(value, optic.asTarget[C])

  ///////////////////////////////////
  // dot syntax for optics typeclass
  ///////////////////////////////////

  override def _1(implicit ev: Field1[B]): AppliedLens[A, ev.B] = first(ev)
  override def _2(implicit ev: Field2[B]): AppliedLens[A, ev.B] = second(ev)
  override def _3(implicit ev: Field3[B]): AppliedLens[A, ev.B] = third(ev)
  override def _4(implicit ev: Field4[B]): AppliedLens[A, ev.B] = fourth(ev)
  override def _5(implicit ev: Field5[B]): AppliedLens[A, ev.B] = fifth(ev)
  override def _6(implicit ev: Field6[B]): AppliedLens[A, ev.B] = sixth(ev)

  override def first(implicit ev: Field1[B]): AppliedLens[A, ev.B]  = compose(ev.first)
  override def second(implicit ev: Field2[B]): AppliedLens[A, ev.B] = compose(ev.second)
  override def third(implicit ev: Field3[B]): AppliedLens[A, ev.B]  = compose(ev.third)
  override def fourth(implicit ev: Field4[B]): AppliedLens[A, ev.B] = compose(ev.fourth)
  override def fifth(implicit ev: Field5[B]): AppliedLens[A, ev.B]  = compose(ev.fifth)
  override def sixth(implicit ev: Field6[B]): AppliedLens[A, ev.B]  = compose(ev.sixth)

  override def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedLens[A, Option[C]] = compose(ev.at(i))
  override def cons(implicit ev: Cons[B]): AppliedPrism[A, (ev.B, B)]                  = compose(ev.cons)
  override def reverse(implicit ev: Reverse[B]): AppliedIso[A, ev.B]                   = compose(ev.reverse)

  ///////////////////////////////////
  // dot syntax for standard types
  ///////////////////////////////////

  override def left[E, C](implicit ev: B =:= Either[E, C]): AppliedPrism[A, E] =
    asTarget[Either[E, C]].compose(Prism.left[E, C])
  override def right[E, C](implicit ev: B =:= Either[E, C]): AppliedPrism[A, C] =
    asTarget[Either[E, C]].compose(Prism.right[E, C])
  override def some[C](implicit ev: B =:= Option[C]): AppliedPrism[A, C] = asTarget[Option[C]].compose(Prism.some[C])
}

object AppliedIso {
  def apply[A, B](_value: A, _optic: Iso[A, B]): AppliedIso[A, B] =
    new AppliedIso[A, B] {
      def value: A         = _value
      def optic: Iso[A, B] = _optic
    }

  def id[A](value: A): AppliedIso[A, A] =
    apply(value, Iso.id)
}
