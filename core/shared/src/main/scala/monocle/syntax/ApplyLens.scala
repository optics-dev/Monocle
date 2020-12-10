package monocle.syntax

import cats.{Eq, Functor}
import monocle.function.{At, Each, Index}
import monocle.{std, Fold, Getter, Optional, PIso, PLens, POptional, PPrism, PSetter, PTraversal}

final case class ApplyLens[S, T, A, B](s: S, lens: PLens[S, T, A, B]) {
  def get: A                                     = lens.get(s)
  def replace(b: B): T                           = lens.replace(b)(s)
  def modify(f: A => B): T                       = lens.modify(f)(s)
  def modifyF[F[_]: Functor](f: A => F[B]): F[T] = lens.modifyF(f)(s)
  def exist(p: A => Boolean): S => Boolean       = lens.exist(p)
  def find(p: A => Boolean): S => Option[A]      = lens.find(p)

  /** alias to replace */
  @deprecated("use replace instead", since = "3.0.0-M1")
  def set(b: B): T = replace(b)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplyOptional[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplyLens[S, T, A1, B1] =
    evB.substituteCo[ApplyLens[S, T, A1, *]](evA.substituteCo[ApplyLens[S, T, *, B]](this))

  def andThen[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, lens.andThen(other))
  def andThen[C](other: Fold[A, C]): ApplyFold[S, C] =
    ApplyFold(s, lens.andThen(other))
  def andThen[C](other: Getter[A, C]): ApplyGetter[S, C] =
    ApplyGetter(s, lens.andThen(other))
  def andThen[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, lens.andThen(other))
  def andThen[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, lens.andThen(other))
  def andThen[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, lens.andThen(other))
  def andThen[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] =
    ApplyLens(s, lens.andThen(other))
  def andThen[C, D](other: PIso[A, B, C, D]): ApplyLens[S, T, C, D] =
    ApplyLens(s, lens.andThen(other))

  def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D]          = andThen(other)
  def composeFold[C](other: Fold[A, C]): ApplyFold[S, C]                                = andThen(other)
  def composeGetter[C](other: Getter[A, C]): ApplyGetter[S, C]                          = andThen(other)
  def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = andThen(other)
  def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D]    = andThen(other)
  def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D]          = andThen(other)
  def composeLens[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D]                = andThen(other)
  def composeIso[C, D](other: PIso[A, B, C, D]): ApplyLens[S, T, C, D]                  = andThen(other)

  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] = andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] = andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] = andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): ApplyLens[S, T, C, D] = andThen(other)
}

object ApplyLens {
  implicit def applyLensSyntax[S, A](self: ApplyLens[S, S, A, A]): ApplyLensSyntax[S, A] =
    new ApplyLensSyntax(self)
}

/** Extension methods for monomorphic ApplyLens */
final case class ApplyLensSyntax[S, A](private val self: ApplyLens[S, S, A, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): ApplyTraversal[S, S, C, C] =
    self composeTraversal evEach.each

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): ApplyOptional[S, S, A, A] =
    self.andThen(Optional.filter(predicate))

  def withDefault[A1: Eq](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): ApplyLens[S, S, A1, A1] =
    self.adapt[Option[A1], Option[A1]] composeIso (std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): ApplyLens[S, S, A1, A1] =
    self composeLens evAt.at(i)

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): ApplyOptional[S, S, A1, A1] =
    self composeOptional evIndex.index(i)
}
