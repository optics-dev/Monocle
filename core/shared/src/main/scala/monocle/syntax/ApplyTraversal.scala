package monocle.syntax

import cats.{Applicative, Eq}
import monocle.function.Each
import monocle.{std, Fold, PIso, PLens, POptional, PPrism, PSetter, PTraversal}

final case class ApplyTraversal[S, T, A, B](s: S, traversal: PTraversal[S, T, A, B]) {
  @inline def getAll: List[A]       = traversal.getAll(s)
  @inline def headOption: Option[A] = traversal.headOption(s)
  @inline def lastOption: Option[A] = traversal.lastOption(s)

  @inline def replace(b: B): T     = traversal.replace(b)(s)
  @inline def modify(f: A => B): T = traversal.modify(f)(s)
  @inline def modifyF[F[_]: Applicative](f: A => F[B]): F[T] =
    traversal.modifyF(f)(s)

  @inline def find(p: A => Boolean): S => Option[A] = traversal.find(p)
  @inline def exist(p: A => Boolean): S => Boolean  = traversal.exist(p)
  @inline def all(p: A => Boolean): S => Boolean    = traversal.all(p)
  @inline def isEmpty(s: S): Boolean                = traversal.isEmpty(s)
  @inline def nonEmpty(s: S): Boolean               = traversal.nonEmpty(s)

  /** alias to replace */
  @deprecated("use replace instead", since = "3.0.0-M1")
  @inline def set(b: B): T = replace(b)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplyTraversal[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplyTraversal[S, T, A1, B1] =
    evB.substituteCo[ApplyTraversal[S, T, A1, *]](evA.substituteCo[ApplyTraversal[S, T, *, B]](this))

  def andThen[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, traversal.andThen(other))
  def andThen[C](other: Fold[A, C]): ApplyFold[S, C] =
    ApplyFold(s, traversal.andThen(other))
  def andThen[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, traversal.andThen(other))
  def andThen[C, D](other: POptional[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, traversal.andThen(other))
  def andThen[C, D](other: PPrism[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, traversal.andThen(other))
  def andThen[C, D](other: PLens[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, traversal.andThen(other))
  def andThen[C, D](other: PIso[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, traversal.andThen(other))

  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D]          = andThen(other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C]                                = andThen(other)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = andThen(other)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyTraversal[S, T, C, D]   = andThen(other)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyTraversal[S, T, C, D]         = andThen(other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyTraversal[S, T, C, D]           = andThen(other)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): ApplyTraversal[S, T, C, D]             = andThen(other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = andThen(other)

  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyTraversal[S, T, C, D] = andThen(other)

  /** alias to composePrism */
  @inline def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyTraversal[S, T, C, D] = andThen(other)

  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyTraversal[S, T, C, D] = andThen(other)

  /** alias to composeIso */
  @inline def ^<->[C, D](other: PIso[A, B, C, D]): ApplyTraversal[S, T, C, D] = andThen(other)
}

object ApplyTraversal {
  implicit def applyTraversalSyntax[S, A](self: ApplyTraversal[S, S, A, A]): ApplyTraversalSyntax[S, A] =
    new ApplyTraversalSyntax(self)
}

/** Extension methods for monomorphic ApplyTraversal */
final case class ApplyTraversalSyntax[S, A](private val self: ApplyTraversal[S, S, A, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): ApplyTraversal[S, S, C, C] =
    self composeTraversal evEach.each

  def withDefault[A1: Eq](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): ApplyTraversal[S, S, A1, A1] =
    self.adapt[Option[A1], Option[A1]] composeIso (std.option.withDefault(defaultValue))
}
