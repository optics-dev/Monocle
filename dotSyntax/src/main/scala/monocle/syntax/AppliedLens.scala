package monocle.syntax

import monocle.{Lens, Prism}
import monocle.function._

trait AppliedLens[From, To] extends AppliedOptional[From, To] with AppliedGetter[From, To] {
  def value: From
  def optic: Lens[From, To]

  def andThen[X](other: Lens[To, X]): AppliedLens[From, X] =
    AppliedLens(value, optic.andThen(other))

  override def asTarget[X](implicit ev: To =:= X): AppliedLens[From, X] =
    AppliedLens(value, optic.asTarget[X])

  override def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): AppliedLens[From, Option[X]] =
    andThen(ev.at(i))

  override def some[X](implicit ev: To =:= Option[X]): AppliedOptional[From, X] =
    asTarget[Option[X]].andThen(Prism.some[X])
}

object AppliedLens {
  def apply[From, To](_value: From, _optic: Lens[From, To]): AppliedLens[From, To] =
    new AppliedLens[From, To] {
      def value: From           = _value
      def optic: Lens[From, To] = _optic
    }
}
