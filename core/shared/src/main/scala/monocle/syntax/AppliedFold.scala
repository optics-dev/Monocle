package monocle.syntax

import cats.Monoid
import monocle.function.{At, Each, FilterIndex, Index}
import monocle._

trait AppliedFold[S, A] {
  def value: S
  def optic: Fold[S, A]

  def foldMap[M: Monoid](f: A => M): M = optic.foldMap(f)(value)

  def getAll: List[A]                  = optic.getAll(value)
  def find(p: A => Boolean): Option[A] = optic.find(p)(value)
  def headOption: Option[A]            = optic.headOption(value)
  def lastOption: Option[A]            = optic.lastOption(value)
  def exist(p: A => Boolean): Boolean  = optic.exist(p)(value)
  def all(p: A => Boolean): Boolean    = optic.all(p)(value)
  def length: Int                      = optic.length(value)
  def isEmpty: Boolean                 = optic.isEmpty(value)
  def nonEmpty: Boolean                = optic.nonEmpty(value)

  def some[A1](implicit ev1: A =:= Option[A1]): AppliedFold[S, A1] =
    adapt[Option[A1]].andThen(std.option.some[A1])

  private[monocle] def adapt[A1](implicit evA: A =:= A1): AppliedFold[S, A1] =
    evA.substituteCo[AppliedFold[S, *]](this)

  def andThen[B](other: Fold[A, B]): AppliedFold[S, B] =
    AppliedFold(value, optic.andThen(other))
}

object AppliedFold {
  def apply[S, A](_value: S, _optic: Fold[S, A]): AppliedFold[S, A] =
    new AppliedFold[S, A] {
      val value: S          = _value
      val optic: Fold[S, A] = _optic
    }

  implicit def appliedFoldSyntax[S, A](self: AppliedFold[S, A]): AppliedFoldSyntax[S, A] =
    new AppliedFoldSyntax(self)
}

final case class AppliedFoldSyntax[S, A](private val self: AppliedFold[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): AppliedFold[S, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): AppliedFold[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): AppliedFold[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): AppliedFold[S, A1] =
    self.adapt[Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): AppliedFold[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): AppliedFold[S, A1] =
    self.andThen(evIndex.index(i))

  /** compose a [[Fold]] with a [[Fold]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): AppliedFold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[C](other: Getter[A, C]): AppliedFold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PTraversal]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[POptional]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[B, C, D](other: POptional[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PPrism]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[B, C, D](other: PPrism[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PLens]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[B, C, D](other: PLens[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[B, C, D](other: PIso[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[B, C, D](other: PTraversal[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[B, C, D](other: POptional[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[B, C, D](other: PPrism[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[B, C, D](other: PLens[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[B, C, D](other: PIso[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)
}
