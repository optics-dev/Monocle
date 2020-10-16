package monocle.syntax

import cats.{Applicative, Eq}
import monocle.function.Each
import monocle.{std, Fold, PIso, PLens, POptional, PPrism, PSetter, PTraversal}

final case class ApplyOptional[S, T, A, B](s: S, optional: POptional[S, T, A, B]) {
  @inline def getOption: Option[A] = optional.getOption(s)

  @inline def isEmpty(s: S): Boolean             = optional.isEmpty(s)
  @inline def nonEmpty(s: S): Boolean            = optional.nonEmpty(s)
  @inline def all(p: A => Boolean): S => Boolean = optional.all(p)

  @inline def exist(p: A => Boolean): S => Boolean  = optional.exist(p)
  @inline def find(p: A => Boolean): S => Option[A] = optional.find(p)

  @inline def modify(f: A => B): T = optional.modify(f)(s)
  @inline def modifyF[F[_]: Applicative](f: A => F[B]): F[T] =
    optional.modifyF(f)(s)
  @inline def modifyOption(f: A => B): Option[T] = optional.modifyOption(f)(s)

  @inline def set(b: B): T               = optional.set(b)(s)
  @inline def setOption(b: B): Option[T] = optional.setOption(b)(s)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplyOptional[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplyOptional[S, T, A1, B1] =
    evB.substituteCo[ApplyOptional[S, T, A1, *]](evA.substituteCo[ApplyOptional[S, T, *, B]](this))

  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, optional composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] =
    ApplyFold(s, optional composeFold other)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, optional composeTraversal other)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional composeOptional other)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional composePrism other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional composeLens other)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[C, D](other: PIso[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composeIso(other)
}

object ApplyOptional {
  implicit def applyOptionalSyntax[S, A](self: ApplyOptional[S, S, A, A]): ApplyOptionalSyntax[S, A] =
    new ApplyOptionalSyntax(self)
}

/** Extension methods for monomorphic ApplyOptional */
final case class ApplyOptionalSyntax[S, A](private val self: ApplyOptional[S, S, A, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): ApplyTraversal[S, S, C, C] =
    self composeTraversal evEach.each

  def withDefault[A1: Eq](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): ApplyOptional[S, S, A1, A1] =
    self.adapt[Option[A1], Option[A1]] composeIso (std.option.withDefault(defaultValue))
}
