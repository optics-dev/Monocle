package monocle.syntax

import monocle.Lens
import monocle.function.{At, Field1, Field2, Field3}

trait AppliedLens[A, B] extends AppliedOptional[A, B]{
  def value: A
  def optic: Lens[A, B]

  def get: B =
    optic.get(value)

  def compose[C](other: Lens[B, C]): AppliedLens[A, C] =
    AppliedLens(value, optic.compose(other))

  override def _1(implicit ev: Field1[B]): AppliedLens[A, ev.B] = first
  override def _2(implicit ev: Field2[B]): AppliedLens[A, ev.B] = second
  override def _3(implicit ev: Field3[B]): AppliedLens[A, ev.B] = third

  override def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedLens[A, Option[C]] =
    compose(ev.at(i))

  override def first(implicit ev: Field1[B]): AppliedLens[A, ev.B] =
    compose(ev.first)

  override def second(implicit ev: Field2[B]): AppliedLens[A, ev.B] =
    compose(ev.second)

  override def third(implicit ev: Field3[B]): AppliedLens[A, ev.B] =
    compose(ev.third)

  override def asTarget[C](implicit ev: B =:= C): AppliedLens[A, C] =
    AppliedLens(value, optic.asTarget[C])

  def field[C](field: B => C): AppliedLens[A, C] = macro monocle.syntax.macros.GenAppliedLensOpsImpl.lens_impl[A, B, C]
}

object AppliedLens {
  def apply[A, B](_value: A, _optic: Lens[A, B]): AppliedLens[A, B] =
    new AppliedLens[A, B] {
      def value: A = _value
      def optic: Lens[A, B] = _optic
    }
}
