package monocle.syntax

import monocle.function._
import monocle.{Fold, Prism}

trait AppliedFold[From, To] {
  def value: From
  def optic: Fold[From, To]

  def map[X](f: To => X): AppliedFold[From, X] =
    AppliedFold(value, optic.map(f))

  def andThen[X](other: Fold[To, X]): AppliedFold[From, X] =
    AppliedFold(value, optic.andThen(other))

  def asTarget[X](implicit ev: To =:= X): AppliedFold[From, X] =
    AppliedFold(value, optic.asTarget[X])

  def toIterator: Iterator[To] =
    optic.toIterator(value)

  def foldLeft[Z](zero: Z)(f: (Z, To) => Z): Z =
    optic.foldLeft(zero)(f)(value)

  def firstOption: Option[To] =
    optic.firstOption(value)

  def lastOption: Option[To] =
    optic.lastOption(value)

  def toList: List[To] =
    optic.toList(value)

  def find(predicate: To => Boolean): Option[To] =
    optic.find(predicate)(value)

  def exist(predicate: To => Boolean): Boolean =
    optic.exist(predicate)(value)

  def forAll(predicate: To => Boolean): Boolean =
    optic.forAll(predicate)(value)

  def length: Int =
    optic.length(value)

  def isEmpty: Boolean =
    optic.isEmpty(value)

  def nonEmpty: Boolean =
    optic.nonEmpty(value)

  def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): AppliedFold[From, Option[X]] =
    andThen(ev.at(i))

  def some[X](implicit ev: To =:= Option[X]): AppliedFold[From, X] =
    asTarget[Option[X]].andThen(Prism.some[X])
}

object AppliedFold {
  def apply[From, To](_value: From, _optic: Fold[From, To]): AppliedFold[From, To] =
    new AppliedFold[From, To] {
      def value: From           = _value
      def optic: Fold[From, To] = _optic
    }
}
