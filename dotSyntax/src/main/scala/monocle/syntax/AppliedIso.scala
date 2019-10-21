package monocle.syntax

import monocle.function.{At, Cons, Field1, Field2}
import monocle.{Iso, Prism}

trait AppliedIso[A, B] extends AppliedLens[A, B] with AppliedPrism[A, B] {
  def value: A
  def optic: Iso[A, B]

  final def compose[C](other: Iso[B, C]): AppliedIso[A, C] =
    AppliedIso(value, optic.compose(other))

  override final def _1(implicit ev: Field1[B]): AppliedLens[A, ev.A] = first
  override final def _2(implicit ev: Field2[B]): AppliedLens[A, ev.A] = second

  override final def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedLens[A, Option[C]] =
    compose(ev.at(i))

  override final def cons(implicit ev: Cons[B]): AppliedPrism[A, (ev.A, B)] =
    compose(ev.cons)

  override final def first(implicit ev: Field1[B]): AppliedLens[A, ev.A] =
    compose(ev.first)

  override final def left[E, C](implicit ev: B =:= Either[E, C]): AppliedPrism[A, E] =
    asTarget[Either[E, C]].compose(Prism.left[E, C])

  override final def right[E, C](implicit ev: B =:= Either[E, C]): AppliedPrism[A, C] =
    asTarget[Either[E, C]].compose(Prism.right[E, C])

  override final def second(implicit ev: Field2[B]): AppliedLens[A, ev.A] =
    compose(ev.second)

  override final def some[C](implicit ev: B =:= Option[C]): AppliedPrism[A, C] =
    asTarget[Option[C]].compose(Prism.some[C])

  override def asTarget[C](implicit ev: B =:= C): AppliedIso[A, C] =
    AppliedIso(value, optic.asTarget[C])
}

object AppliedIso {
  def apply[A, B](_value: A, _optic: Iso[A, B]): AppliedIso[A, B] =
    new AppliedIso[A, B] {
      def value: A = _value
      def optic: Iso[A, B] = _optic
    }

  def id[A](value: A): AppliedIso[A, A] =
    apply(value, Iso.id)
}
