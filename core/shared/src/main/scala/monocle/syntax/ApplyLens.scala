package monocle.syntax

import cats.{Eq, Functor}
import monocle.function.Each
import monocle.{std, Fold, Getter, PIso, PLens, POptional, PPrism, PSetter, PTraversal}

final case class ApplyLens[S, T, A, B](s: S, lens: PLens[S, T, A, B]) {
  @inline def get: A                                     = lens.get(s)
  @inline def set(b: B): T                               = lens.set(b)(s)
  @inline def modify(f: A => B): T                       = lens.modify(f)(s)
  @inline def modifyF[F[_]: Functor](f: A => F[B]): F[T] = lens.modifyF(f)(s)
  @inline def exist(p: A => Boolean): S => Boolean       = lens.exist(p)
  @inline def find(p: A => Boolean): S => Option[A]      = lens.find(p)

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

  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D]          = andThen(other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C]                                = andThen(other)
  @inline def composeGetter[C](other: Getter[A, C]): ApplyGetter[S, C]                          = andThen(other)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = andThen(other)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D]    = andThen(other)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D]          = andThen(other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D]                = andThen(other)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): ApplyLens[S, T, C, D]                  = andThen(other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = andThen(other)

  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] = andThen(other)

  /** alias to composePrism */
  @inline def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] = andThen(other)

  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] = andThen(other)

  /** alias to composeIso */
  @inline def ^<->[C, D](other: PIso[A, B, C, D]): ApplyLens[S, T, C, D] = andThen(other)
}

object ApplyLens {
  implicit def applyLensSyntax[S, A](self: ApplyLens[S, S, A, A]): ApplyLensSyntax[S, A] =
    new ApplyLensSyntax(self)
}

/** Extension methods for monomorphic ApplyLens */
final case class ApplyLensSyntax[S, A](private val self: ApplyLens[S, S, A, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): ApplyTraversal[S, S, C, C] =
    self composeTraversal evEach.each

  def withDefault[A1: Eq](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): ApplyLens[S, S, A1, A1] =
    self.adapt[Option[A1], Option[A1]] composeIso (std.option.withDefault(defaultValue))
}
