package monocle

import scalaz.std.anyVal._
import scalaz.std.list._
import scalaz.std.option._
import scalaz.syntax.std.boolean._
import scalaz.syntax.std.option._
import scalaz.syntax.tag._
import scalaz.{Choice, Foldable, Maybe, Monoid, \/}

/**
 * A [[Fold]] can be seen as a [[Getter]] with many targets or
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

  /**
   * map each target to a Monoid and combine the results
   * underlying representation of [[Fold]], all [[Fold]] methods are defined in terms of foldMap
   */
  def foldMap[M: Monoid](f: A => M)(s: S): M

  /** combine all targets using a target's Monoid */
  @inline final def fold(s: S)(implicit ev: Monoid[A]): A =
    foldMap(identity)(s)

  /**
   * get all the targets of a [[Fold]]
   */
  @inline final def getAll(s: S): List[A] =
    foldMap(List(_))(s)

  /** find the first target of a [[Fold]] matching the predicate  */
  @inline final def find(p: A => Boolean)(s: S): Option[A] =
    foldMap(a => (if(p(a)) Some(a) else None).first)(s).unwrap

  /** get the first target of a [[Fold]] */
  @inline final def headOption(s: S): Option[A] =
    find(_ => true)(s)

  /** check if at least one target satisfies the predicate */
  @inline final def exist(p: A => Boolean)(s: S): Boolean =
    foldMap(p(_).disjunction)(s).unwrap

  /** check if all targets satisfy the predicate */
  @inline final def all(p: A => Boolean)(s: S): Boolean =
    foldMap(p(_).conjunction)(s).unwrap

  /** join two [[Fold]] with the same target */
  @inline final def choice[S1](other: Fold[S1, A]): Fold[S \/ S1, A] =
    new Fold[S \/ S1, A]{
      def foldMap[M: Monoid](f: A => M)(s: S \/ S1): M =
        s.fold(self.foldMap(f), other.foldMap(f))
    }

  @inline final def left[C]: Fold[S \/ C, A \/ C] =
    new Fold[S \/ C, A \/ C]{
      override def foldMap[M: Monoid](f: A \/ C => M)(s: S \/ C): M =
        s.fold(self.foldMap(a => f(\/.left(a))), c => f(\/.right(c)))
    }

  @inline final def right[C]: Fold[C \/ S, C \/ A] =
    new Fold[C \/ S, C \/ A]{
      override def foldMap[M: Monoid](f: C \/ A => M)(s: C \/ S): M =
        s.fold(c => f(\/.left(c)), self.foldMap(a => f(\/.right(a))))
    }

  @deprecated("use choice", since = "1.2.0")
  @inline final def sum[S1](other: Fold[S1, A]): Fold[S \/ S1, A] =
    choice(other)

  @deprecated("use headOption", since = "1.1.0")
  @inline final def headMaybe(s: S): Maybe[A] =
    find(_ => true)(s).toMaybe

  /** calculate the number of targets */
  @inline final def length(s: S): Int =
    foldMap(_ => 1)(s)

  /**********************************************************/
  /** Compose methods between a [[Fold]] and another Optics */
  /**********************************************************/

  /** compose a [[Fold]] with a [[Fold]] */
  @inline final def composeFold[B](other: Fold[A, B]): Fold[S, B] =
    new Fold[S, B] {
      def foldMap[M: Monoid](f: B => M)(s: S): M =
        self.foldMap(other.foldMap(f)(_))(s)
    }

  /** compose a [[Fold]] with a [[Getter]] */
  @inline final def composeGetter[C](other: Getter[A, C]): Fold[S, C] =
    composeFold(other.asFold)

  /** compose a [[Fold]] with a [[PTraversal]] */
  @inline final def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): Fold[S, C] =
    composeFold(other.asFold)

  /** compose a [[Fold]] with a [[POptional]] */
  @inline final def composeOptional[B, C, D](other: POptional[A, B, C, D]): Fold[S, C] =
    composeFold(other.asFold)

  /** compose a [[Fold]] with a [[PPrism]] */
  @inline final def composePrism[B, C, D](other: PPrism[A, B, C, D]): Fold[S, C] =
    composeFold(other.asFold)

  /** compose a [[Fold]] with a [[PLens]] */
  @inline final def composeLens[B, C, D](other: PLens[A, B, C, D]): Fold[S, C] =
    composeFold(other.asFold)

  /** compose a [[Fold]] with a [[PIso]] */
  @inline final def composeIso[B, C, D](other: PIso[A, B, C, D]): Fold[S, C] =
    composeFold(other.asFold)

  /********************************************/
  /** Experimental aliases of compose methods */
  /********************************************/

  /** alias to composeTraversal */
  @inline final def ^|->>[B, C, D](other: PTraversal[A, B, C, D]): Fold[S, C] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline final def ^|-?[B, C, D](other: POptional[A, B, C, D]): Fold[S, C] =
    composeOptional(other)

  /** alias to composePrism */
  @inline final def ^<-?[B, C, D](other: PPrism[A, B, C, D]): Fold[S, C] =
    composePrism(other)

  /** alias to composeLens */
  @inline final def ^|->[B, C, D](other: PLens[A, B, C, D]): Fold[S, C] =
    composeLens(other)

  /** alias to composeIso */
  @inline final def ^<->[B, C, D](other: PIso[A, B, C, D]): Fold[S, C] =
    composeIso(other)

}

object Fold extends FoldInstances {
  def id[A]: Fold[A, A] =
    Iso.id[A].asFold

  def codiagonal[A]: Fold[A \/ A, A] =
    new Fold[A \/ A, A]{
      def foldMap[M: Monoid](f: A => M)(s: A \/ A): M =
        s.fold(f,f)
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
  implicit val foldChoice: Choice[Fold] = new Choice[Fold]{
    def choice[A, B, C](f: => Fold[A, C], g: => Fold[B, C]): Fold[A \/ B, C] =
      f choice g

    def id[A]: Fold[A, A] =
      Fold.id[A]

    def compose[A, B, C](f: Fold[B, C], g: Fold[A, B]): Fold[A, C] =
      g composeFold f
  }
}
