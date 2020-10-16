package monocle.syntax

import cats.{Eq, Monoid}
import monocle.function.Each
import monocle.{std, Fold, Getter, PIso, PLens, POptional, PPrism, PTraversal}

case class ApplyFold[S, A](s: S, _fold: Fold[S, A]) {
  @inline def foldMap[M: Monoid](f: A => M): M = _fold.foldMap(f)(s)

  @inline def getAll: List[A]                       = _fold.getAll(s)
  @inline def find(p: A => Boolean): S => Option[A] = _fold.find(p)
  @inline def headOption: Option[A]                 = _fold.headOption(s)
  @inline def lastOption: Option[A]                 = _fold.lastOption(s)
  @inline def exist(p: A => Boolean): Boolean       = _fold.exist(p)(s)
  @inline def all(p: A => Boolean): Boolean         = _fold.all(p)(s)
  @inline def length: Int                           = _fold.length(s)
  @inline def isEmpty: Boolean                      = _fold.isEmpty(s)
  @inline def nonEmpty: Boolean                     = _fold.nonEmpty(s)

  def each[C](implicit evEach: Each[A, C]): ApplyFold[S, C] =
    composeTraversal(evEach.each)

  def some[A1](implicit ev1: A =:= Option[A1]): ApplyFold[S, A1] =
    adapt[Option[A1]] composePrism (std.option.pSome)

  def withDefault[A1: Eq](defaultValue: A1)(implicit ev1: A =:= Option[A1]): ApplyFold[S, A1] =
    adapt[Option[A1]] composeIso (std.option.withDefault(defaultValue))

  private def adapt[A1](implicit evA: A =:= A1): ApplyFold[S, A1] =
    evA.substituteCo[ApplyFold[S, *]](this)

  @inline def composeFold[B](other: Fold[A, B]): ApplyFold[S, B] =
    ApplyFold(s, _fold composeFold other)
  @inline def composeGetter[B](other: Getter[A, B]): ApplyFold[S, B] =
    ApplyFold(s, _fold composeGetter other)
  @inline def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, _fold composeTraversal other)
  @inline def composeOptional[B, C, D](other: POptional[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, _fold composeOptional other)
  @inline def composePrism[B, C, D](other: PPrism[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, _fold composePrism other)
  @inline def composeLens[B, C, D](other: PLens[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, _fold composeLens other)
  @inline def composeIso[B, C, D](other: PIso[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, _fold composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[B, C, D](other: PTraversal[A, B, C, D]): ApplyFold[S, C] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[B, C, D](other: POptional[A, B, C, D]): ApplyFold[S, C] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[B, C, D](other: PPrism[A, B, C, D]): ApplyFold[S, C] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[B, C, D](other: PLens[A, B, C, D]): ApplyFold[S, C] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[B, C, D](other: PIso[A, B, C, D]): ApplyFold[S, C] =
    composeIso(other)
}
