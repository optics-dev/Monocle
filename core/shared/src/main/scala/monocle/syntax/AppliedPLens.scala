package monocle.syntax

import cats.Functor
import monocle._
import monocle.function.At

trait AppliedPLens[S, T, A, B] extends AppliedPOptional[S, T, A, B] with AppliedGetter[S, A] {
  override def optic: PLens[S, T, A, B]

  def modifyF[F[_]: Functor](f: A => F[B]): F[T] =
    optic.modifyF(f)(value)

  override def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): AppliedPOptional[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  override private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): AppliedPLens[S, T, A1, B1] =
    evB.substituteCo[AppliedPLens[S, T, A1, *]](evA.substituteCo[AppliedPLens[S, T, *, B]](this))

  def andThen[C, D](other: PLens[A, B, C, D]): AppliedPLens[S, T, C, D] =
    AppliedPLens(value, optic.andThen(other))

}

object AppliedPLens {
  def apply[S, T, A, B](_value: S, _optic: PLens[S, T, A, B]): AppliedPLens[S, T, A, B] =
    new AppliedPLens[S, T, A, B] {
      val value: S                 = _value
      val optic: PLens[S, T, A, B] = _optic
    }

  implicit def appliedPLensSyntax[S, T, A, B](self: AppliedPLens[S, T, A, B]): AppliedPLensSyntax[S, T, A, B] =
    new AppliedPLensSyntax(self)

  implicit def appliedLensSyntax[S, A](self: AppliedLens[S, A]): AppliedLensSyntax[S, A] =
    new AppliedLensSyntax(self)
}

object AppliedLens {
  def apply[S, A](_value: S, _optic: Lens[S, A]): AppliedLens[S, A] =
    AppliedPLens(_value, _optic)
}

final case class AppliedPLensSyntax[S, T, A, B](private val self: AppliedPLens[S, T, A, B]) extends AnyVal {

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): AppliedFold[S, C] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[C](other: Getter[A, C]): AppliedGetter[S, C] =
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
  def composeLens[C, D](other: PLens[A, B, C, D]): AppliedPLens[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): AppliedPLens[S, T, C, D] =
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
  def ^|->[C, D](other: PLens[A, B, C, D]): AppliedPLens[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): AppliedPLens[S, T, C, D] =
    self.andThen(other)
}

final case class AppliedLensSyntax[S, A](private val self: AppliedLens[S, A]) extends AnyVal {
  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): AppliedLens[S, A1] =
    self.adapt[Option[A1], Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, I, A1]): AppliedLens[S, A1] =
    self.andThen(evAt.at(i))
}
