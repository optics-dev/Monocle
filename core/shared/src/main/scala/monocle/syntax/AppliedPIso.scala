package monocle.syntax

import monocle.function.{At, Each, FilterIndex, Index}
import monocle._

trait AppliedPIso[S, T, A, B] extends AppliedPLens[S, T, A, B] with AppliedPPrism[S, T, A, B] {

  override def optic: PIso[S, T, A, B]

  override def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): AppliedPPrism[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  override private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): AppliedPIso[S, T, A1, B1] =
    evB.substituteCo[AppliedPIso[S, T, A1, *]](evA.substituteCo[AppliedPIso[S, T, *, B]](this))

  def andThen[C, D](other: PIso[A, B, C, D]): AppliedPIso[S, T, C, D] =
    AppliedPIso(value, optic.andThen(other))
}

object AppliedPIso {
  def apply[S, T, A, B](_value: S, _optic: PIso[S, T, A, B]): AppliedPIso[S, T, A, B] =
    new AppliedPIso[S, T, A, B] {
      val value: S                = _value
      val optic: PIso[S, T, A, B] = _optic
    }

  implicit def appliedPIsoSyntax[S, T, A, B](self: AppliedPIso[S, T, A, B]): AppliedPIsoSyntax[S, T, A, B] =
    new AppliedPIsoSyntax(self)

  implicit def appliedIsoSyntax[S, A](self: AppliedIso[S, A]): AppliedIsoSyntax[S, A] =
    new AppliedIsoSyntax(self)
}

object AppliedIso {
  def apply[S, A](_value: S, _optic: Iso[S, A]): AppliedIso[S, A] =
    AppliedPIso(_value, _optic)
}

final case class AppliedPIsoSyntax[S, T, A, B](private val self: AppliedPIso[S, T, A, B]) extends AnyVal {

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
  def composePrism[C, D](other: PPrism[A, B, C, D]): AppliedPPrism[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[C, D](other: PLens[A, B, C, D]): AppliedPLens[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): AppliedPIso[S, T, C, D] =
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
  def ^|->[C, D](other: PLens[A, B, C, D]): AppliedPLens[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): AppliedPIso[S, T, C, D] =
    self.andThen(other)
}

final case class AppliedIsoSyntax[S, A](private val self: AppliedIso[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): AppliedTraversal[S, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate. This combinator can break the fusion property see
    * Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): AppliedOptional[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): AppliedTraversal[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): AppliedIso[S, A1] =
    self.adapt[Option[A1], Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, I, A1]): AppliedLens[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): AppliedOptional[S, A1] =
    self.andThen(evIndex.index(i))
}
