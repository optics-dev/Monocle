package monocle

import cats.{Eq, Foldable, Monoid}
import cats.arrow.Choice
import cats.instances.int._
import cats.instances.list._
import cats.syntax.either._
import monocle.function.{At, Each, FilterIndex, Index}
import monocle.internal.Monoids

/** A [[Fold]] can be seen as a [[Getter]] with many targets or
  * a weaker [[PTraversal]] which cannot modify its target.
  *
  * [[Fold]] is on the top of the Optic hierarchy which means that
  * [[Getter]], [[PTraversal]], [[POptional]], [[PLens]], [[PPrism]]
  * and [[PIso]] are valid [[Fold]]
  *
  * @tparam S the source of a [[Fold]]
  * @tparam A the target of a [[Fold]]
  */
abstract class Fold[S, A] extends Serializable { self =>

  /** map each target to a Monoid and combine the results
    * underlying representation of [[Fold]], all [[Fold]] methods are defined in terms of foldMap
    */
  def foldMap[M: Monoid](f: A => M)(s: S): M

  /** combine all targets using a target's Monoid */
  final def fold(s: S)(implicit ev: Monoid[A]): A =
    foldMap(identity)(s)

  /** get all the targets of a [[Fold]] */
  final def getAll(s: S): List[A] =
    foldMap(List(_))(s)

  /** find the first target matching the predicate */
  final def find(p: A => Boolean): S => Option[A] =
    foldMap(a => Some(a).filter(p))(_)(Monoids.firstOption)

  /** get the first target */
  final def headOption(s: S): Option[A] =
    foldMap(Option(_))(s)(Monoids.firstOption)

  /** get the last target */
  final def lastOption(s: S): Option[A] =
    foldMap(Option(_))(s)(Monoids.lastOption)

  /** check if at least one target satisfies the predicate */
  final def exist(p: A => Boolean): S => Boolean =
    foldMap(p(_))(_)(Monoids.any)

  /** check if all targets satisfy the predicate */
  final def all(p: A => Boolean): S => Boolean =
    foldMap(p(_))(_)(Monoids.all)

  /** calculate the number of targets */
  final def length(s: S): Int =
    foldMap(_ => 1)(s)

  /** check if there is no target */
  final def isEmpty(s: S): Boolean =
    foldMap(_ => false)(s)(Monoids.all)

  /** check if there is at least one target */
  final def nonEmpty(s: S): Boolean =
    !isEmpty(s)

  /** join two [[Fold]] with the same target */
  final def choice[S1](other: Fold[S1, A]): Fold[Either[S, S1], A] =
    new Fold[Either[S, S1], A] {
      def foldMap[M: Monoid](f: A => M)(s: Either[S, S1]): M =
        s.fold(self.foldMap(f), other.foldMap(f))
    }

  final def left[C]: Fold[Either[S, C], Either[A, C]] =
    new Fold[Either[S, C], Either[A, C]] {
      override def foldMap[M: Monoid](f: Either[A, C] => M)(s: Either[S, C]): M =
        s.fold(self.foldMap(a => f(Either.left(a))), c => f(Either.right(c)))
    }

  final def right[C]: Fold[Either[C, S], Either[C, A]] =
    new Fold[Either[C, S], Either[C, A]] {
      override def foldMap[M: Monoid](f: Either[C, A] => M)(s: Either[C, S]): M =
        s.fold(c => f(Either.left(c)), self.foldMap(a => f(Either.right(a))))
    }

  /** Compose with a function lifted into a Getter */
  def to[C](f: A => C): Fold[S, C] =
    andThen(Getter(f))

  def each[C](implicit evEach: Each[A, C]): Fold[S, C] =
    andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): Fold[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): Fold[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def some[A1](implicit ev1: A =:= Option[A1]): Fold[S, A1] =
    adapt[Option[A1]].andThen(std.option.some[A1])

  def withDefault[A1: Eq](defaultValue: A1)(implicit ev1: A =:= Option[A1]): Fold[S, A1] =
    adapt[Option[A1]].andThen(std.option.withDefault(defaultValue))

  private def adapt[A1](implicit evA: A =:= A1): Fold[S, A1] =
    evA.substituteCo[Fold[S, *]](this)

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): Fold[S, A1] =
    andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): Fold[S, A1] =
    andThen(evIndex.index(i))

  /** compose a [[Fold]] with another [[Fold]] */
  final def andThen[B](other: Fold[A, B]): Fold[S, B] =
    new Fold[S, B] {
      def foldMap[M: Monoid](f: B => M)(s: S): M =
        self.foldMap(other.foldMap(f)(_))(s)
    }

  /** compose a [[Fold]] with a [[Getter]] */
  final def andThen[C](other: Getter[A, C]): Fold[S, C] =
    andThen(other.asFold)

  /** compose a [[Fold]] with a [[PTraversal]] */
  final def andThen[B, C, D](other: PTraversal[A, B, C, D]): Fold[S, C] =
    andThen(other.asFold)

  /** compose a [[Fold]] with a [[POptional]] */
  final def andThen[B, C, D](other: POptional[A, B, C, D]): Fold[S, C] =
    andThen(other.asFold)

  /** compose a [[Fold]] with a [[PPrism]] */
  final def andThen[B, C, D](other: PPrism[A, B, C, D]): Fold[S, C] =
    andThen(other.asFold)

  /** compose a [[Fold]] with a [[PLens]] */
  final def andThen[B, C, D](other: PLens[A, B, C, D]): Fold[S, C] =
    andThen(other.asFold)

  /** compose a [[Fold]] with a [[PIso]] */
  final def andThen[B, C, D](other: PIso[A, B, C, D]): Fold[S, C] =
    andThen(other.asFold)

  /** compose a [[Fold]] with a [[Fold]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeFold[B](other: Fold[A, B]): Fold[S, B] =
    andThen(other)

  /** compose a [[Fold]] with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeGetter[C](other: Getter[A, C]): Fold[S, C] =
    andThen(other.asFold)

  /** compose a [[Fold]] with a [[PTraversal]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): Fold[S, C] =
    andThen(other.asFold)

  /** compose a [[Fold]] with a [[POptional]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeOptional[B, C, D](other: POptional[A, B, C, D]): Fold[S, C] =
    andThen(other.asFold)

  /** compose a [[Fold]] with a [[PPrism]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composePrism[B, C, D](other: PPrism[A, B, C, D]): Fold[S, C] =
    andThen(other.asFold)

  /** compose a [[Fold]] with a [[PLens]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeLens[B, C, D](other: PLens[A, B, C, D]): Fold[S, C] =
    andThen(other.asFold)

  /** compose a [[Fold]] with a [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeIso[B, C, D](other: PIso[A, B, C, D]): Fold[S, C] =
    andThen(other.asFold)

  /** *****************************************
    */
  /** Experimental aliases of compose methods */
  /** *****************************************
    */
  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^|->>[B, C, D](other: PTraversal[A, B, C, D]): Fold[S, C] =
    andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^|-?[B, C, D](other: POptional[A, B, C, D]): Fold[S, C] =
    andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^<-?[B, C, D](other: PPrism[A, B, C, D]): Fold[S, C] =
    andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^|->[B, C, D](other: PLens[A, B, C, D]): Fold[S, C] =
    andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^<->[B, C, D](other: PIso[A, B, C, D]): Fold[S, C] =
    andThen(other)
}

object Fold extends FoldInstances {
  def id[A]: Fold[A, A] =
    Iso.id[A].asFold

  def codiagonal[A]: Fold[Either[A, A], A] =
    new Fold[Either[A, A], A] {
      def foldMap[M: Monoid](f: A => M)(s: Either[A, A]): M =
        s.fold(f, f)
    }

  def select[A](p: A => Boolean): Fold[A, A] =
    new Fold[A, A] {
      def foldMap[M: Monoid](f: A => M)(s: A): M =
        if (p(s)) f(s) else Monoid[M].empty
    }

  /** [[Fold]] that points to nothing */
  def void[S, A]: Fold[S, A] =
    Optional.void.asFold

  /** create a [[Fold]] from a Foldable */
  def fromFoldable[F[_]: Foldable, A]: Fold[F[A], A] =
    new Fold[F[A], A] {
      def foldMap[M: Monoid](f: A => M)(s: F[A]): M =
        Foldable[F].foldMap(s)(f)
    }
}

sealed abstract class FoldInstances {
  implicit val foldChoice: Choice[Fold] = new Choice[Fold] {
    def choice[A, B, C](f: Fold[A, C], g: Fold[B, C]): Fold[Either[A, B], C] =
      f choice g

    def id[A]: Fold[A, A] =
      Fold.id[A]

    def compose[A, B, C](f: Fold[B, C], g: Fold[A, B]): Fold[A, C] =
      g.andThen(f)
  }
}
