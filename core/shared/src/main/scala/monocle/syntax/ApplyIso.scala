package monocle.syntax

import cats.{Eq, Functor}
import monocle.function.Each
import monocle.{std, Fold, Getter, Iso, PIso, PLens, POptional, PPrism, PSetter, PTraversal, Traversal}

final case class ApplyIso[S, T, A, B](s: S, iso: PIso[S, T, A, B]) {
  @inline def get: A                                     = iso.get(s)
  @inline def set(b: B): T                               = iso.set(b)(s)
  @inline def modify(f: A => B): T                       = iso.modify(f)(s)
  @inline def modifyF[F[_]: Functor](f: A => F[B]): F[T] = iso.modifyF(f)(s)
  @inline def exist(p: A => Boolean): S => Boolean       = iso.exist(p)
  @inline def find(p: A => Boolean): S => Option[A]      = iso.find(p)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplyPrism[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplyIso[S, T, A1, B1] =
    evB.substituteCo[ApplyIso[S, T, A1, *]](evA.substituteCo[ApplyIso[S, T, *, B]](this))

  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, iso composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] =
    ApplyFold(s, iso composeFold other)
  @inline def composeGetter[C](other: Getter[A, C]): ApplyGetter[S, C] =
    ApplyGetter(s, iso composeGetter other)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, iso composeTraversal other)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, iso composeOptional other)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyPrism[S, T, C, D] =
    ApplyPrism(s, iso composePrism other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] =
    ApplyLens(s, iso composeLens other)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): ApplyIso[S, T, C, D] =
    ApplyIso(s, iso composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyPrism[S, T, C, D] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[C, D](other: PIso[A, B, C, D]): ApplyIso[S, T, C, D] =
    composeIso(other)
}

object ApplyIso {
  implicit def applyIsoSyntax[S, A](self: ApplyIso[S, S, A, A]): ApplyIsoSyntax[S, A] =
    new ApplyIsoSyntax(self)
}

/** Extension methods for monomorphic ApplyIso */
final case class ApplyIsoSyntax[S, A](private val self: ApplyIso[S, S, A, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): ApplyTraversal[S, S, C, C] =
    self composeTraversal evEach.each

  def withDefault[A1: Eq](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): ApplyIso[S, S, A1, A1] =
    self.adapt[Option[A1], Option[A1]] composeIso (std.option.withDefault(defaultValue))
}
