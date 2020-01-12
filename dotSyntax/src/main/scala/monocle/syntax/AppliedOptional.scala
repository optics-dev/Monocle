package monocle.syntax

import monocle.{Optional, Prism}
import monocle.function._

trait AppliedOptional[From, To] extends AppliedFold[From, To] with AppliedSetter[From, To] {
  def value: From
  def optic: Optional[From, To]

  def getOption: Option[To] =
    optic.getOption(value)

  def set(to: To): From =
    optic.set(to)(value)

  def modify(f: To => To): From =
    optic.modify(f)(value)

  def andThen[C](other: Optional[To, C]): AppliedOptional[From, C] =
    AppliedOptional(value, optic.andThen(other))

  override def asTarget[X](implicit ev: To =:= X): AppliedOptional[From, X] =
    AppliedOptional(value, optic.asTarget[X])

  override def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): AppliedOptional[From, Option[X]] =
    andThen(ev.at(i))

  override def some[X](implicit ev: To =:= Option[X]): AppliedOptional[From, X] =
    asTarget[Option[X]].andThen(Prism.some[X])
}

object AppliedOptional {
  def apply[From, To](_value: From, _optic: Optional[From, To]): AppliedOptional[From, To] =
    new AppliedOptional[From, To] {
      def value: From               = _value
      def optic: Optional[From, To] = _optic
    }
}
