package monocle.syntax

import monocle.{Prism, Setter}
import monocle.function._

trait AppliedSetter[From, To] {
  def value: From
  def optic: Setter[From, To]

  def set: To => From =
    optic.set(_)(value)

  def compose[X](other: Setter[To, X]): AppliedSetter[From, X] =
    AppliedSetter(value, optic.compose(other))

  def asTarget[X](implicit ev: To =:= X): AppliedSetter[From, X] =
    AppliedSetter(value, optic.asTarget[X])

  def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): AppliedSetter[From, Option[X]] =
    compose(ev.at(i))

  def some[X](implicit ev: To =:= Option[X]): AppliedSetter[From, X] =
    asTarget[Option[X]].compose(Prism.some[X])
}

object AppliedSetter {
  def apply[From, To](_value: From, _optic: Setter[From, To]): AppliedSetter[From, To] =
    new AppliedSetter[From, To] {
      def value: From             = _value
      def optic: Setter[From, To] = _optic
    }
}
