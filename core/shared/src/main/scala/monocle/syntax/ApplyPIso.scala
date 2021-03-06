package monocle.syntax

import cats.Functor
import monocle.function.{At, Each, FilterIndex, Index}
import monocle._

final case class ApplyPIso[S, T, A, B](s: S, iso: PIso[S, T, A, B]) {
  def get: A                                     = iso.get(s)
  def replace(b: B): T                           = iso.replace(b)(s)
  def modify(f: A => B): T                       = iso.modify(f)(s)
  def modifyF[F[_]: Functor](f: A => F[B]): F[T] = iso.modifyF(f)(s)
  def exist(p: A => Boolean): Boolean            = iso.exist(p)(s)
  def find(p: A => Boolean): Option[A]           = iso.find(p)(s)

  @deprecated("use replace instead", since = "3.0.0-M1")
  def set(b: B): T = replace(b)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplyPPrism[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplyPIso[S, T, A1, B1] =
    evB.substituteCo[ApplyPIso[S, T, A1, *]](evA.substituteCo[ApplyPIso[S, T, *, B]](this))

  def andThen[C, D](other: PSetter[A, B, C, D]): ApplyPSetter[S, T, C, D] =
    ApplyPSetter(s, iso.andThen(other))
  def andThen[C](other: Fold[A, C]): ApplyFold[S, C] =
    ApplyFold(s, iso.andThen(other))
  def andThen[C](other: Getter[A, C]): ApplyGetter[S, C] =
    ApplyGetter(s, iso.andThen(other))
  def andThen[C, D](other: PTraversal[A, B, C, D]): ApplyPTraversal[S, T, C, D] =
    ApplyPTraversal(s, iso.andThen(other))
  def andThen[C, D](other: POptional[A, B, C, D]): ApplyPOptional[S, T, C, D] =
    ApplyPOptional(s, iso.andThen(other))
  def andThen[C, D](other: PPrism[A, B, C, D]): ApplyPPrism[S, T, C, D] =
    ApplyPPrism(s, iso.andThen(other))
  def andThen[C, D](other: PLens[A, B, C, D]): ApplyPLens[S, T, C, D] =
    ApplyPLens(s, iso.andThen(other))
  def andThen[C, D](other: PIso[A, B, C, D]): ApplyPIso[S, T, C, D] =
    ApplyPIso(s, iso.andThen(other))

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplyPSetter[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[C](other: Getter[A, C]): ApplyGetter[S, C] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyPTraversal[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyPOptional[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyPPrism[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[C, D](other: PLens[A, B, C, D]): ApplyPLens[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): ApplyPIso[S, T, C, D] = andThen(other)

  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyPTraversal[S, T, C, D] = andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyPOptional[S, T, C, D] = andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyPPrism[S, T, C, D] = andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[C, D](other: PLens[A, B, C, D]): ApplyPLens[S, T, C, D] = andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): ApplyPIso[S, T, C, D] = andThen(other)
}

object ApplyPIso {
  implicit def applyIsoSyntax[S, A](self: ApplyIso[S, A]): ApplyIsoSyntax[S, A] =
    new ApplyIsoSyntax(self)
}

/** Extension methods for monomorphic ApplyIso */
final case class ApplyIsoSyntax[S, A](private val self: ApplyIso[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): ApplyTraversal[S, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): ApplyOptional[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): ApplyTraversal[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): ApplyIso[S, A1] =
    self.adapt[Option[A1], Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): ApplyLens[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): ApplyOptional[S, A1] =
    self.andThen(evIndex.index(i))
}
