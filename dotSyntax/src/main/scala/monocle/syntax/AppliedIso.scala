package monocle.syntax

import monocle.function._
import monocle.{Iso, Prism}

trait AppliedIso[A, B] extends AppliedLens[A, B] with AppliedPrism[A, B] {
  def value: A
  def optic: Iso[A, B]

  def compose[C](other: Iso[B, C]): AppliedIso[A, C] =
    AppliedIso(value, optic.compose(other))

  override def asTarget[C](implicit ev: B =:= C): AppliedIso[A, C] =
    AppliedIso(value, optic.asTarget[C])

  override def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedLens[A, Option[C]] =
    compose(ev.at(i))

  override def some[C](implicit ev: B =:= Option[C]): AppliedPrism[A, C] =
    asTarget[Option[C]].compose(Prism.some[C])
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
