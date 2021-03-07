package monocle.syntax

import cats.Applicative
import monocle.function.{At, Each, FilterIndex, Index}
import monocle._

final case class AppliedPTraversal[S, T, A, B](s: S, traversal: PTraversal[S, T, A, B]) {
  def getAll: List[A]       = traversal.getAll(s)
  def headOption: Option[A] = traversal.headOption(s)
  def lastOption: Option[A] = traversal.lastOption(s)

  def replace(b: B): T     = traversal.replace(b)(s)
  def modify(f: A => B): T = traversal.modify(f)(s)
  def modifyA[F[_]: Applicative](f: A => F[B]): F[T] =
    traversal.modifyA(f)(s)

  def find(p: A => Boolean): Option[A] = traversal.find(p)(s)
  def exist(p: A => Boolean): Boolean  = traversal.exist(p)(s)
  def all(p: A => Boolean): Boolean    = traversal.all(p)(s)
  def isEmpty(s: S): Boolean           = traversal.isEmpty(s)
  def nonEmpty(s: S): Boolean          = traversal.nonEmpty(s)

  /** alias to replace */
  @deprecated("use replace instead", since = "3.0.0-M1")
  def set(b: B): T = replace(b)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): AppliedPTraversal[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): AppliedPTraversal[S, T, A1, B1] =
    evB.substituteCo[AppliedPTraversal[S, T, A1, *]](evA.substituteCo[AppliedPTraversal[S, T, *, B]](this))

  def andThen[C, D](other: PSetter[A, B, C, D]): AppliedPSetter[S, T, C, D] =
    AppliedPSetter(s, traversal.andThen(other))
  def andThen[C](other: Fold[A, C]): AppliedFold[S, C] =
    AppliedFold(s, traversal.andThen(other))
  def andThen[C, D](other: PTraversal[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    AppliedPTraversal(s, traversal.andThen(other))
  def andThen[C, D](other: POptional[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    AppliedPTraversal(s, traversal.andThen(other))
  def andThen[C, D](other: PPrism[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    AppliedPTraversal(s, traversal.andThen(other))
  def andThen[C, D](other: PLens[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    AppliedPTraversal(s, traversal.andThen(other))
  def andThen[C, D](other: PIso[A, B, C, D]): AppliedPTraversal[S, T, C, D] =
    AppliedPTraversal(s, traversal.andThen(other))

  @deprecated("use andThen", since = "3.0.0-M1")
  def composeSetter[C, D](other: PSetter[A, B, C, D]): AppliedPSetter[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): AppliedFold[S, C] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[C, D](other: PTraversal[A, B, C, D]): AppliedPTraversal[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[C, D](other: POptional[A, B, C, D]): AppliedPTraversal[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[C, D](other: PPrism[A, B, C, D]): AppliedPTraversal[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[C, D](other: PLens[A, B, C, D]): AppliedPTraversal[S, T, C, D] = andThen(other)
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): AppliedPTraversal[S, T, C, D] = andThen(other)

  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[C, D](other: PTraversal[A, B, C, D]): AppliedPTraversal[S, T, C, D] = andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[C, D](other: POptional[A, B, C, D]): AppliedPTraversal[S, T, C, D] = andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[C, D](other: PPrism[A, B, C, D]): AppliedPTraversal[S, T, C, D] = andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[C, D](other: PLens[A, B, C, D]): AppliedPTraversal[S, T, C, D] = andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): AppliedPTraversal[S, T, C, D] = andThen(other)
}

object AppliedPTraversal {
  implicit def appliedTraversalSyntax[S, A](self: AppliedTraversal[S, A]): AppliedTraversalSyntax[S, A] =
    new AppliedTraversalSyntax(self)
}

/** Extension methods for monomorphic ApplyTraversal */
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

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): AppliedTraversal[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): AppliedTraversal[S, A1] =
    self.andThen(evIndex.index(i))
}
