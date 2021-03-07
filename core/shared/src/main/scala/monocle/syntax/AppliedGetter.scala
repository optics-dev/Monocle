package monocle.syntax

import monocle._
import monocle.function.At

trait AppliedGetter[S, A] extends AppliedFold[S, A] {

  override def optic: Getter[S, A]

  def get: A = optic.get(value)

  override def some[A1](implicit ev1: A =:= Option[A1]): AppliedFold[S, A1] =
    adapt[Option[A1]].andThen(std.option.some[A1])

  override private[monocle] def adapt[A1](implicit evA: A =:= A1): AppliedGetter[S, A1] =
    evA.substituteCo[AppliedGetter[S, *]](this)

  def andThen[B](other: Getter[A, B]): AppliedGetter[S, B] =
    AppliedGetter(value, optic.andThen(other))
}

object AppliedGetter {
  def apply[S, A](_value: S, _optic: Getter[S, A]): AppliedGetter[S, A] =
    new AppliedGetter[S, A] {
      val value: S            = _value
      val optic: Getter[S, A] = _optic
    }

  implicit def appliedGetterSyntax[S, A](self: AppliedGetter[S, A]): AppliedGetterSyntax[S, A] =
    new AppliedGetterSyntax(self)
}

final case class AppliedGetterSyntax[S, A](private val self: AppliedGetter[S, A]) extends AnyVal {

  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): AppliedGetter[S, A1] =
    self.adapt[Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): AppliedGetter[S, A1] =
    self.andThen(evAt.at(i))

  /** compose a [[Fold]] with a [[Fold]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): AppliedFold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[C](other: Getter[A, C]): AppliedGetter[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PTraversal]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[POptional]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[B, C, D](other: POptional[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PPrism]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[B, C, D](other: PPrism[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PLens]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[B, C, D](other: PLens[A, B, C, D]): AppliedGetter[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[B, C, D](other: PIso[A, B, C, D]): AppliedGetter[S, C] =
    self.andThen(other)

  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[B, C, D](other: PTraversal[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[B, C, D](other: POptional[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[B, C, D](other: PPrism[A, B, C, D]): AppliedFold[S, C] =
    self.andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[B, C, D](other: PLens[A, B, C, D]): AppliedGetter[S, C] =
    self.andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[B, C, D](other: PIso[A, B, C, D]): AppliedGetter[S, C] =
    self.andThen(other)
}
