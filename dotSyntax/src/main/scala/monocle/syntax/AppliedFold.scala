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

  def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): AppliedFold[A, Option[C]] =
    compose(ev.at(i))

  def some[C](implicit ev: B =:= Option[C]): AppliedFold[A, C] =
    asTarget[Option[C]].compose(Prism.some[C])
}

object AppliedFold {
  def apply[A, B](_value: A, _optic: Fold[A, B]): AppliedFold[A, B] =
    new AppliedFold[A, B] {
      def value: A          = _value
      def optic: Fold[A, B] = _optic
    }
}
