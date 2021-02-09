package monocle.syntax

import cats.{Applicative, Eq}
import monocle.function.{At, Each, FilterIndex, Index}
import monocle.{std, Fold, Optional, PIso, PLens, POptional, PPrism, PSetter, PTraversal}

final case class ApplyOptional[S, T, A, B](s: S, optional: POptional[S, T, A, B]) {
  def getOption: Option[A] = optional.getOption(s)

  def isEmpty(s: S): Boolean        = optional.isEmpty(s)
  def nonEmpty(s: S): Boolean       = optional.nonEmpty(s)
  def all(p: A => Boolean): Boolean = optional.all(p)(s)

  def exist(p: A => Boolean): Boolean  = optional.exist(p)(s)
  def find(p: A => Boolean): Option[A] = optional.find(p)(s)

  def modify(f: A => B): T = optional.modify(f)(s)
  def modifyF[F[_]: Applicative](f: A => F[B]): F[T] =
    optional.modifyF(f)(s)
  def modifyOption(f: A => B): Option[T] = optional.modifyOption(f)(s)

  def replace(b: B): T               = optional.replace(b)(s)
  def replaceOption(b: B): Option[T] = optional.replaceOption(b)(s)

  /** alias to replace */
  @deprecated("use replace instead", since = "3.0.0-M1")
  def set(b: B): T = replace(b)

  /** alias to replace */
  @deprecated("use replaceOption instead", since = "3.0.0-M1")
  def setOption(b: B): Option[T] = replaceOption(b)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplyOptional[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplyOptional[S, T, A1, B1] =
    evB.substituteCo[ApplyOptional[S, T, A1, *]](evA.substituteCo[ApplyOptional[S, T, *, B]](this))

  def andThen[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, optional.andThen(other))
  def andThen[C](other: Fold[A, C]): ApplyFold[S, C] =
    ApplyFold(s, optional.andThen(other))
  def andThen[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, optional.andThen(other))
  def andThen[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional.andThen(other))
  def andThen[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional.andThen(other))
  def andThen[C, D](other: PLens[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional.andThen(other))
  def andThen[C, D](other: PIso[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional.andThen(other))

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[C, D](other: PLens[A, B, C, D]): ApplyOptional[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): ApplyOptional[S, T, C, D] = andThen(other)

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
  def ^|->[C, D](other: PLens[A, B, C, D]): ApplyOptional[S, T, C, D] = andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): ApplyOptional[S, T, C, D] = andThen(other)
}

object ApplyOptional {
  implicit def applyOptionalSyntax[S, A](self: ApplyOptional[S, S, A, A]): ApplyOptionalSyntax[S, A] =
    new ApplyOptionalSyntax(self)
}

/** Extension methods for monomorphic ApplyOptional */
final case class ApplyOptionalSyntax[S, A](private val self: ApplyOptional[S, S, A, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): ApplyTraversal[S, S, C, C] =
    self composeTraversal evEach.each

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): ApplyOptional[S, S, A, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): ApplyTraversal[S, S, A1, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1: Eq](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): ApplyOptional[S, S, A1, A1] =
    self.adapt[Option[A1], Option[A1]] composeIso (std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): ApplyOptional[S, S, A1, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): ApplyOptional[S, S, A1, A1] =
    self.andThen(evIndex.index(i))
}
