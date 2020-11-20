package monocle.syntax

import cats.Eq
import monocle.function.Each
import monocle.{std, Fold, Getter, PIso, PLens, POptional, PPrism, PTraversal}

final case class ApplyGetter[S, A](s: S, getter: Getter[S, A]) {
  @inline def get: A                                = getter.get(s)
  @inline def exist(p: A => Boolean): S => Boolean  = getter.exist(p)
  @inline def find(p: A => Boolean): S => Option[A] = getter.find(p)

  def each[C](implicit evEach: Each[A, C]): ApplyFold[S, C] =
    composeTraversal(evEach.each)

  def some[A1](implicit ev1: A =:= Option[A1]): ApplyFold[S, A1] =
    adapt[Option[A1]] composePrism (std.option.pSome)

  def withDefault[A1: Eq](defaultValue: A1)(implicit ev1: A =:= Option[A1]): ApplyGetter[S, A1] =
    adapt[Option[A1]] composeIso (std.option.withDefault(defaultValue))

  private def adapt[A1](implicit evA: A =:= A1): ApplyGetter[S, A1] =
    evA.substituteCo[ApplyGetter[S, *]](this)

  def andThen[B](other: Fold[A, B]): ApplyFold[S, B] =
    ApplyFold(s, getter.andThen(other))
  def andThen[B](other: Getter[A, B]): ApplyGetter[S, B] =
    ApplyGetter(s, getter.andThen(other))
  def andThen[B, C, D](other: PTraversal[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, getter.andThen(other))
  def andThen[B, C, D](other: POptional[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, getter.andThen(other))
  def andThen[B, C, D](other: PPrism[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, getter.andThen(other))
  def andThen[B, C, D](other: PLens[A, B, C, D]): ApplyGetter[S, C] =
    ApplyGetter(s, getter.andThen(other))
  def andThen[B, C, D](other: PIso[A, B, C, D]): ApplyGetter[S, C] =
    ApplyGetter(s, getter.andThen(other))

  @inline def composeFold[B](other: Fold[A, B]): ApplyFold[S, B] = andThen(other)
  @inline def composeGetter[B](other: Getter[A, B]): ApplyGetter[S, B] = andThen(other)
  @inline def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): ApplyFold[S, C] = andThen(other)
  @inline def composeOptional[B, C, D](other: POptional[A, B, C, D]): ApplyFold[S, C] = andThen(other)
  @inline def composePrism[B, C, D](other: PPrism[A, B, C, D]): ApplyFold[S, C] = andThen(other)
  @inline def composeLens[B, C, D](other: PLens[A, B, C, D]): ApplyGetter[S, C] = andThen(other)
  @inline def composeIso[B, C, D](other: PIso[A, B, C, D]): ApplyGetter[S, C] = andThen(other)

  /** alias to composeLens */
  @inline def ^|->[B, C, D](other: PLens[A, B, C, D]): ApplyGetter[S, C] = andThen(other)
  /** alias to composeIso */
  @inline def ^<->[B, C, D](other: PIso[A, B, C, D]): ApplyGetter[S, C] = andThen(other)
}
