package monocle.syntax

import monocle.function.{At, Each, FilterIndex, Index}
import monocle._

final case class AppliedPSetter[S, T, A, B](s: S, setter: PSetter[S, T, A, B]) {
  def replace(b: B): T     = setter.replace(b)(s)
  def modify(f: A => B): T = setter.modify(f)(s)

  /** alias to replace */
  @deprecated("use replace instead", since = "3.0.0-M1")
  def set(b: B): T = replace(b)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): AppliedPSetter[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): AppliedPSetter[S, T, A1, B1] =
    evB.substituteCo[AppliedPSetter[S, T, A1, *]](evA.substituteCo[AppliedPSetter[S, T, *, B]](this))

  def andThen[C, D](other: PSetter[A, B, C, D]): AppliedPSetter[S, T, C, D] =
    AppliedPSetter(s, setter.andThen(other))
  def andThen[C, D](other: PTraversal[A, B, C, D]): AppliedPSetter[S, T, C, D] =
    AppliedPSetter(s, setter.andThen(other))
  def andThen[C, D](other: POptional[A, B, C, D]): AppliedPSetter[S, T, C, D] =
    AppliedPSetter(s, setter.andThen(other))
  def andThen[C, D](other: PPrism[A, B, C, D]): AppliedPSetter[S, T, C, D] =
    AppliedPSetter(s, setter.andThen(other))
  def andThen[C, D](other: PLens[A, B, C, D]): AppliedPSetter[S, T, C, D] =
    AppliedPSetter(s, setter.andThen(other))
  def andThen[C, D](other: PIso[A, B, C, D]): AppliedPSetter[S, T, C, D] =
    AppliedPSetter(s, setter.andThen(other))

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeSetter[C, D](other: PSetter[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[C, D](other: PTraversal[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[C, D](other: POptional[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[C, D](other: PPrism[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[C, D](other: PLens[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)

  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[C, D](other: PTraversal[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[C, D](other: POptional[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[C, D](other: PPrism[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[C, D](other: PLens[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)
}

object AppliedPSetter {
  implicit def appliedSetterSyntax[S, A](self: AppliedSetter[S, A]): AppliedSetterSyntax[S, A] =
    new AppliedSetterSyntax(self)
}

/** Extension methods for monomorphic ApplySetter */
final case class AppliedSetterSyntax[S, A](private val self: AppliedSetter[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): AppliedPSetter[S, S, C, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): AppliedSetter[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): AppliedSetter[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): AppliedSetter[S, A1] =
    self.adapt[Option[A1], Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): AppliedSetter[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): AppliedSetter[S, A1] =
    self.andThen(evIndex.index(i))
}
