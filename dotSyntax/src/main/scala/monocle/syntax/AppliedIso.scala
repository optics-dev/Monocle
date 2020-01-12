package monocle.syntax

import monocle.function._
import monocle.{Iso, Prism}

trait AppliedIso[From, To] extends AppliedLens[From, To] with AppliedPrism[From, To] {
  def value: From
  def optic: Iso[From, To]

  def andThen[C](other: Iso[To, C]): AppliedIso[From, C] =
    AppliedIso(value, optic.andThen(other))

  override def asTarget[X](implicit ev: To =:= X): AppliedIso[From, X] =
    AppliedIso(value, optic.asTarget[X])

  override def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): AppliedLens[From, Option[X]] =
    andThen(ev.at(i))

  override def some[X](implicit ev: To =:= Option[X]): AppliedPrism[From, X] =
    asTarget[Option[X]].andThen(Prism.some[X])
}

object AppliedIso {
  def apply[From, To](_value: From, _optic: Iso[From, To]): AppliedIso[From, To] =
    new AppliedIso[From, To] {
      def value: From          = _value
      def optic: Iso[From, To] = _optic
    }

  def id[From](value: From): AppliedIso[From, From] =
    apply(value, Iso.id)
}
