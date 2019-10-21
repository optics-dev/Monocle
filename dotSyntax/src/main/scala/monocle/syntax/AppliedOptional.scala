package monocle.syntax

import monocle.Optional
import monocle.function.{At, Cons, Field1, Field2, Index}

trait AppliedOptional[A, B] {
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

  def _1(implicit ev: Field1[B]): AppliedOptional[A, ev.A] = first
  def _2(implicit ev: Field2[B]): AppliedOptional[A, ev.A] = second

  def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedOptional[A, Option[C]] =
    compose(ev.at(i))

  def cons(implicit ev: Cons[B]): AppliedOptional[A, (ev.A, B)] =
    compose(ev.cons)

  def first(implicit ev: Field1[B]): AppliedOptional[A, ev.A] =
    compose(ev.first)

  def headOption(implicit ev: Cons[B]): AppliedOptional[A, ev.A] =
   compose(ev.headOption)

  def index[I, C](i: I)(implicit ev: Index.Aux[B, I, C]): AppliedOptional[A, C] =
    compose(ev.index(i))

  def second(implicit ev: Field2[B]): AppliedOptional[A, ev.A] =
    compose(ev.second)

  def tailOption(implicit ev: Cons[B]): AppliedOptional[A, B] =
    compose(ev.tailOption)
}

object AppliedOptional {
  def apply[A, B](_value: A, _optic: Optional[A, B]): AppliedOptional[A, B] =
    new AppliedOptional[A, B] {
      def value: A = _value
      def optic: Optional[A, B] = _optic
    }
}