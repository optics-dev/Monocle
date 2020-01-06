package monocle.syntax

import monocle.{Lens, Prism}
import monocle.function._

trait AppliedLens[A, B] extends AppliedOptional[A, B] with AppliedGetter[A, B] {
  def value: A
  def optic: Lens[A, B]

  def compose[C](other: Lens[B, C]): AppliedLens[A, C] =
    AppliedLens(value, optic.compose(other))

  override def asTarget[C](implicit ev: B =:= C): AppliedLens[A, C] =
    AppliedLens(value, optic.asTarget[C])

  override def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedLens[A, Option[C]] =
    compose(ev.at(i))

  override def some[C](implicit ev: B =:= Option[C]): AppliedOptional[A, C] =
    asTarget[Option[C]].compose(Prism.some[C])
}

object AppliedLens {
  def apply[A, B](_value: A, _optic: Lens[A, B]): AppliedLens[A, B] =
    new AppliedLens[A, B] {
      def value: A          = _value
      def optic: Lens[A, B] = _optic
    }
}
