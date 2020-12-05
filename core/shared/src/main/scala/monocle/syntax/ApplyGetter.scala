package monocle.syntax

import cats.Eq
import monocle.function.{At, Each, Index}
import monocle.{std, Fold, Getter, PIso, PLens, POptional, PPrism, PTraversal}

final case class ApplyGetter[S, A](s: S, getter: Getter[S, A]) {
  @inline def get: A                                = getter.get(s)
  @inline def exist(p: A => Boolean): S => Boolean  = getter.exist(p)
  @inline def find(p: A => Boolean): S => Option[A] = getter.find(p)

  def some[A1](implicit ev1: A =:= Option[A1]): ApplyFold[S, A1] =
    adapt[Option[A1]] composePrism (std.option.pSome)

  private[monocle] def adapt[A1](implicit evA: A =:= A1): ApplyGetter[S, A1] =
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

  @inline def composeFold[B](other: Fold[A, B]): ApplyFold[S, B]                        = andThen(other)
  @inline def composeGetter[B](other: Getter[A, B]): ApplyGetter[S, B]                  = andThen(other)
  @inline def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): ApplyFold[S, C] = andThen(other)
  @inline def composeOptional[B, C, D](other: POptional[A, B, C, D]): ApplyFold[S, C]   = andThen(other)
  @inline def composePrism[B, C, D](other: PPrism[A, B, C, D]): ApplyFold[S, C]         = andThen(other)
  @inline def composeLens[B, C, D](other: PLens[A, B, C, D]): ApplyGetter[S, C]         = andThen(other)
  @inline def composeIso[B, C, D](other: PIso[A, B, C, D]): ApplyGetter[S, C]           = andThen(other)

  /** alias to composeLens */
  @inline def ^|->[B, C, D](other: PLens[A, B, C, D]): ApplyGetter[S, C] = andThen(other)

  /** alias to composeIso */
  @inline def ^<->[B, C, D](other: PIso[A, B, C, D]): ApplyGetter[S, C] = andThen(other)
}

object ApplyGetter {
  implicit def applyGetterSyntax[S, A](self: ApplyGetter[S, A]): ApplyGetterSyntax[S, A] =
    new ApplyGetterSyntax(self)
}

final case class ApplyGetterSyntax[S, A](self: ApplyGetter[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): ApplyFold[S, C] =
    self composeTraversal evEach.each

  def withDefault[A1: Eq](defaultValue: A1)(implicit ev1: A =:= Option[A1]): ApplyGetter[S, A1] =
    self.adapt[Option[A1]] composeIso std.option.withDefault(defaultValue)

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): ApplyGetter[S, A1] =
    self composeLens evAt.at(i)

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): ApplyFold[S, A1] =
    self composeOptional evIndex.index(i)
}
