package monocle.syntax

import monocle.{Prism, Setter}
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

  def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedSetter[A, Option[C]] =
    compose(ev.at(i))

  def some[C](implicit ev: B =:= Option[C]): AppliedSetter[A, C] =
    asTarget[Option[C]].compose(Prism.some[C])

}

object AppliedSetter {
  def apply[A, B](_value: A, _optic: Setter[A, B]): AppliedSetter[A, B] =
    new AppliedSetter[A, B] {
      def value: A            = _value
      def optic: Setter[A, B] = _optic
    }
}
