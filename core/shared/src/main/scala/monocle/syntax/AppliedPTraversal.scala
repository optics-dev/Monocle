package monocle.syntax

import cats.Applicative
import monocle.function.{At, Each, FilterIndex, Index}
import monocle._

trait AppliedPTraversal[S, T, A, B] extends AppliedPSetter[S, T, A, B] with AppliedFold[S, A] {
  override def optic: PTraversal[S, T, A, B]

  def modifyA[F[_]: Applicative](f: A => F[B]): F[T] =
    optic.modifyA(f)(value)

  override def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): AppliedPTraversal[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  override private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): AppliedPTraversal[S, T, A1, B1] =
    evB.substituteCo[AppliedPTraversal[S, T, A1, *]](evA.substituteCo[AppliedPTraversal[S, T, *, B]](this))

  def andThen[C, D](other: PTraversal[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    AppliedPTraversal(value, optic.andThen(other))
}

object AppliedPTraversal {
  def apply[S, T, A, B](_value: S, _optic: PTraversal[S, T, A, B]): AppliedPTraversal[S, T, A, B] =
    new AppliedPTraversal[S, T, A, B] {
      val value: S                      = _value
      val optic: PTraversal[S, T, A, B] = _optic
    }

  implicit def appliedPTraversalSyntax[S, T, A, B](
    self: AppliedPTraversal[S, T, A, B]
  ): AppliedPTraversalSyntax[S, T, A, B] =
    new AppliedPTraversalSyntax(self)

  implicit def appliedTraversalSyntax[S, A](self: AppliedTraversal[S, A]): AppliedTraversalSyntax[S, A] =
    new AppliedTraversalSyntax(self)
}

object AppliedTraversal {
  def apply[S, A](_value: S, _optic: Traversal[S, A]): AppliedTraversal[S, A] =
    AppliedPTraversal(_value, _optic)
}

final case class AppliedPTraversalSyntax[S, T, A, B](private val self: AppliedPTraversal[S, T, A, B]) extends AnyVal {

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
  def composeOptional[C, D](other: POptional[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[C, D](other: PPrism[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[C, D](other: PLens[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[C, D](other: PTraversal[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[C, D](other: POptional[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[C, D](other: PPrism[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[C, D](other: PLens[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)

  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    self.andThen(other)
}

/** Extension methods for monomorphic Traversal
  */
final case class AppliedTraversalSyntax[S, A](private val self: AppliedTraversal[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): AppliedTraversal[S, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): AppliedTraversal[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): AppliedTraversal[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): AppliedTraversal[S, A1] =
    self.adapt[Option[A1], Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, I, A1]): AppliedTraversal[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): AppliedTraversal[S, A1] =
    self.andThen(evIndex.index(i))
}
