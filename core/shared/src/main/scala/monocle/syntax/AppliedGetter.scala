package monocle.syntax

import monocle.function.{At, Each, FilterIndex, Index}
import monocle.{std, Fold, Getter, Optional, PIso, PLens, POptional, PPrism, PTraversal}

final case class AppliedGetter[S, A](s: S, getter: Getter[S, A]) {
  def get: A                           = getter.get(s)
  def exist(p: A => Boolean): Boolean  = getter.exist(p)(s)
  def find(p: A => Boolean): Option[A] = getter.find(p)(s)

  def each[C](implicit evEach: Each[A, C]): AppliedFold[S, C] =
    andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): AppliedFold[S, A] =
    andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): AppliedFold[S, A1] =
    andThen(ev.filterIndex(predicate))

  def some[A1](implicit ev1: A =:= Option[A1]): AppliedFold[S, A1] =
    adapt[Option[A1]].andThen(std.option.some[A1])

  def withDefault[A1](defaultValue: A1)(implicit ev1: A =:= Option[A1]): AppliedGetter[S, A1] =
    adapt[Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): AppliedGetter[S, A1] =
    andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): AppliedFold[S, A1] =
    andThen(evIndex.index(i))

  private def adapt[A1](implicit evA: A =:= A1): AppliedGetter[S, A1] =
    evA.substituteCo[AppliedGetter[S, *]](this)

  def andThen[B](other: Fold[A, B]): AppliedFold[S, B] =
    AppliedFold(s, getter.andThen(other))
  def andThen[B](other: Getter[A, B]): AppliedGetter[S, B] =
    AppliedGetter(s, getter.andThen(other))
  def andThen[B, C, D](other: PTraversal[A, B, C, D]): AppliedFold[S, C] =
    AppliedFold(s, getter.andThen(other))
  def andThen[B, C, D](other: POptional[A, B, C, D]): AppliedFold[S, C] =
    AppliedFold(s, getter.andThen(other))
  def andThen[B, C, D](other: PPrism[A, B, C, D]): AppliedFold[S, C] =
    AppliedFold(s, getter.andThen(other))
  def andThen[B, C, D](other: PLens[A, B, C, D]): AppliedGetter[S, C] =
    AppliedGetter(s, getter.andThen(other))
  def andThen[B, C, D](other: PIso[A, B, C, D]): AppliedGetter[S, C] =
    AppliedGetter(s, getter.andThen(other))

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[B](other: Fold[A, B]): AppliedFold[S, B] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[B](other: Getter[A, B]): AppliedGetter[S, B] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): AppliedFold[S, C] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[B, C, D](other: POptional[A, B, C, D]): AppliedFold[S, C] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[B, C, D](other: PPrism[A, B, C, D]): AppliedFold[S, C] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[B, C, D](other: PLens[A, B, C, D]): AppliedGetter[S, C] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[B, C, D](other: PIso[A, B, C, D]): AppliedGetter[S, C] = andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[B, C, D](other: PLens[A, B, C, D]): AppliedGetter[S, C] = andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[B, C, D](other: PIso[A, B, C, D]): AppliedGetter[S, C] = andThen(other)
}
