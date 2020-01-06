package monocle.syntax

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

  override def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedOptional[A, Option[C]] =
    compose(ev.at(i))

  override def some[C](implicit ev: B =:= Option[C]): AppliedOptional[A, C] =
    asTarget[Option[C]].compose(Prism.some[C])
}

object AppliedOptional {
  def apply[A, B](_value: A, _optic: Optional[A, B]): AppliedOptional[A, B] =
    new AppliedOptional[A, B] {
      def value: A              = _value
      def optic: Optional[A, B] = _optic
    }
}
