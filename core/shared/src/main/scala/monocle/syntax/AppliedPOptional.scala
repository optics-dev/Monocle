package monocle.syntax

import monocle._
import monocle.function.{At, Index}

trait AppliedPOptional[S, T, A, B] extends AppliedPTraversal[S, T, A, B] {
  override def optic: POptional[S, T, A, B]

  def getOption: Option[A]               = optic.getOption(value)
  def modifyOption(f: A => B): Option[T] = optic.modifyOption(f)(value)
  def replaceOption(b: B): Option[T]     = optic.replaceOption(b)(value)

  /** alias to replace */
  @deprecated("use replaceOption instead", since = "3.0.0-M1")
  def setOption(b: B): Option[T] = replaceOption(b)

  override def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): AppliedPOptional[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  override private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): AppliedPOptional[S, T, A1, B1] =
    evB.substituteCo[AppliedPOptional[S, T, A1, *]](evA.substituteCo[AppliedPOptional[S, T, *, B]](this))

  def andThen[C, D](other: POptional[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    AppliedPOptional(value, optic.andThen(other))
}

object AppliedPOptional {
  def apply[S, T, A, B](_value: S, _optic: POptional[S, T, A, B]): AppliedPOptional[S, T, A, B] =
    new AppliedPOptional[S, T, A, B] {
      val value: S                     = _value
      val optic: POptional[S, T, A, B] = _optic
    }

  implicit def appliedPOptionalSyntax[S, T, A, B](
    self: AppliedPOptional[S, T, A, B]
  ): AppliedPOptionalSyntax[S, T, A, B] =
    new AppliedPOptionalSyntax(self)

  implicit def appliedOptionalSyntax[S, A](self: AppliedOptional[S, A]): AppliedOptionalSyntax[S, A] =
    new AppliedOptionalSyntax(self)
}

object AppliedOptional {
  def apply[S, A](_value: S, _optic: Optional[S, A]): AppliedOptional[S, A] =
    AppliedPOptional(_value, _optic)
}

final case class AppliedPOptionalSyntax[S, T, A, B](private val self: AppliedPOptional[S, T, A, B]) extends AnyVal {

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): AppliedFold[S, C] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[C](other: Getter[A, C]): AppliedFold[S, C] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeSetter[C, D](other: PSetter[A, B, C, D]): AppliedPSetter[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[C, D](other: PTraversal[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[C, D](other: POptional[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[C, D](other: PPrism[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[C, D](other: PLens[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[C, D](other: PTraversal[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[C, D](other: POptional[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[C, D](other: PPrism[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[C, D](other: PLens[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    self.andThen(other)
}

/** Extension methods for monomorphic Optional
  */
final case class AppliedOptionalSyntax[S, A](private val self: AppliedOptional[S, A]) extends AnyVal {

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): AppliedOptional[S, A] =
    self.andThen(Optional.filter(predicate))

  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): AppliedOptional[S, A1] =
    self.adapt[Option[A1], Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, I, A1]): AppliedOptional[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): AppliedOptional[S, A1] =
    self.andThen(evIndex.index(i))
}
