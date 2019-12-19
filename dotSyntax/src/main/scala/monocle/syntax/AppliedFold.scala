package monocle.syntax

import monocle.function._
import monocle.{Fold, Prism}

trait AppliedFold[A, B] {
  def value: A
  def optic: Fold[A, B]

  def map[C](f: B => C): AppliedFold[A, C] =
    AppliedFold(value, optic.map(f))

  def compose[C](other: Fold[B, C]): AppliedFold[A, C] =
    AppliedFold(value, optic.compose(other))

  def asTarget[C](implicit ev: B =:= C): AppliedFold[A, C] =
    AppliedFold(value, optic.asTarget[C])

  def toIterator: Iterator[B] =
    optic.toIterator(value)

  def foldLeft[Z](zero: Z)(f: (Z, B) => Z): Z =
    optic.foldLeft(zero)(f)(value)

  def firstOption: Option[B] =
    optic.firstOption(value)

  def lastOption: Option[B] =
    optic.lastOption(value)

  def toList: List[B] =
    optic.toList(value)

  def find(predicate: B => Boolean): Option[B] =
    optic.find(predicate)(value)

  def exist(predicate: B => Boolean): Boolean =
    optic.exist(predicate)(value)

  def forAll(predicate: B => Boolean): Boolean =
    optic.forAll(predicate)(value)

  def length: Int =
    optic.length(value)

  def isEmpty: Boolean =
    optic.isEmpty(value)

  def nonEmpty: Boolean =
    optic.nonEmpty(value)

  def _1(implicit ev: Field1[B]): AppliedFold[A, ev.B] = first
  def _2(implicit ev: Field2[B]): AppliedFold[A, ev.B] = second
  def _3(implicit ev: Field3[B]): AppliedFold[A, ev.B] = third
  def _4(implicit ev: Field4[B]): AppliedFold[A, ev.B] = fourth
  def _5(implicit ev: Field5[B]): AppliedFold[A, ev.B] = fifth
  def _6(implicit ev: Field6[B]): AppliedFold[A, ev.B] = sixth

  def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedFold[A, Option[C]] =
    compose(ev.at(i))

  def cons(implicit ev: Cons[B]): AppliedFold[A, (ev.B, B)] =
    compose(ev.cons)

  def first(implicit ev: Field1[B]): AppliedFold[A, ev.B] =
    compose(ev.first)

  def headOption(implicit ev: Cons[B]): AppliedFold[A, ev.B] =
    compose(ev.headOption)

  def index[I, C](i: I)(implicit ev: Index.Aux[B, I, C]): AppliedFold[A, C] =
    compose(ev.index(i))

  def left[E, C](implicit ev: B =:= Either[E, C]): AppliedFold[A, E] =
    asTarget[Either[E, C]].compose(Prism.left[E, C])

  def right[E, C](implicit ev: B =:= Either[E, C]): AppliedFold[A, C] =
    asTarget[Either[E, C]].compose(Prism.right[E, C])

  def second(implicit ev: Field2[B]): AppliedFold[A, ev.B] =
    compose(ev.second)

  def third(implicit ev: Field3[B]): AppliedFold[A, ev.B] =
    compose(ev.third)

  def fourth(implicit ev: Field4[B]): AppliedFold[A, ev.B] =
    compose(ev.fourth)

  def fifth(implicit ev: Field5[B]): AppliedFold[A, ev.B] =
    compose(ev.fifth)

  def sixth(implicit ev: Field6[B]): AppliedFold[A, ev.B] =
    compose(ev.sixth)

  def reverse(implicit ev: Reverse[B]): AppliedFold[A, ev.B] =
    compose(ev.reverse)

  def some[C](implicit ev: B =:= Option[C]): AppliedFold[A, C] =
    asTarget[Option[C]].compose(Prism.some[C])

  def tailOption(implicit ev: Cons[B]): AppliedFold[A, B] =
    compose(ev.tailOption)

  def possible(implicit ev: Possible[B]): AppliedFold[A, ev.B] =
    compose(ev.possible)
}


object AppliedFold {
  def apply[A, B](_value: A, _optic: Fold[A, B]): AppliedFold[A, B] =
    new AppliedFold[A, B] {
      def value: A          = _value
      def optic: Fold[A, B] = _optic
    }
}
