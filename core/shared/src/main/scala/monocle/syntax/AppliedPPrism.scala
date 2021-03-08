package monocle.syntax

import monocle._

trait AppliedPPrism[S, T, A, B] extends AppliedPOptional[S, T, A, B] {
  override def optic: PPrism[S, T, A, B]

  override def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): AppliedPPrism[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  override private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): AppliedPPrism[S, T, A1, B1] =
    evB.substituteCo[AppliedPPrism[S, T, A1, *]](evA.substituteCo[AppliedPPrism[S, T, *, B]](this))

  def andThen[C, D](other: PPrism[A, B, C, D]): AppliedPPrism[S, T, C, D] =
    AppliedPPrism(value, optic.andThen(other))

}

object AppliedPPrism {
  def apply[S, T, A, B](_value: S, _optic: PPrism[S, T, A, B]): AppliedPPrism[S, T, A, B] =
    new AppliedPPrism[S, T, A, B] {
      val value: S                  = _value
      val optic: PPrism[S, T, A, B] = _optic
    }

  implicit def appliedPPrismSyntax[S, T, A, B](self: AppliedPPrism[S, T, A, B]): AppliedPPrismSyntax[S, T, A, B] =
    new AppliedPPrismSyntax(self)

  implicit def appliedPrismSyntax[S, A](self: AppliedPrism[S, A]): AppliedPrismSyntax[S, A] =
    new AppliedPrismSyntax(self)
}

object AppliedPrism {
  def apply[S, A](_value: S, _optic: Prism[S, A]): AppliedPrism[S, A] =
    AppliedPPrism(_value, _optic)
}

final case class AppliedPPrismSyntax[S, T, A, B](private val self: AppliedPPrism[S, T, A, B]) extends AnyVal {

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
  def composeLens[C, D](other: PLens[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[C, D](other: PPrism[A, B, C, D]): AppliedPPrism[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): AppliedPPrism[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[C, D](other: PTraversal[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[C, D](other: POptional[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[C, D](other: PPrism[A, B, C, D]): AppliedPPrism[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[C, D](other: PLens[A, B, C, D]): AppliedPOptional[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): AppliedPPrism[S, T, C, D] =
    self.andThen(other)
}

final case class AppliedPrismSyntax[S, A](private val self: AppliedPrism[S, A]) extends AnyVal {
  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): AppliedPrism[S, A1] =
    self.adapt[Option[A1], Option[A1]].andThen(std.option.withDefault(defaultValue))
}
