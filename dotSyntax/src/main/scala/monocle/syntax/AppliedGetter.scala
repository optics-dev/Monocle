package monocle.syntax

import monocle.Getter
import monocle.function._

trait AppliedGetter[From, To] extends AppliedFold[From, To] {
  def value: From
  def optic: Getter[From, To]

  def get: To =
    optic.get(value)

  def andThen[X](other: Getter[To, X]): AppliedGetter[From, X] =
    AppliedGetter(value, optic.andThen(other))

  override def asTarget[X](implicit ev: To =:= X): AppliedGetter[From, X] =
    AppliedGetter(value, optic.asTarget[X])

  override def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): AppliedGetter[From, Option[X]] =
    andThen(ev.at(i))
}

object AppliedGetter {
  def apply[From, To](_value: From, _optic: Getter[From, To]): AppliedGetter[From, To] =
    new AppliedGetter[From, To] {
      def value: From             = _value
      def optic: Getter[From, To] = _optic
    }
}
