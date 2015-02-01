package monocle

import scalaz.Maybe._
import scalaz.std.anyVal._
import scalaz.syntax.std.boolean._
import scalaz.syntax.tag._
import scalaz.{Choice, Foldable, IList, Maybe, Monoid, \/}

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
abstract class Fold[S, A] { self =>

  /**
   * map each target to a [[Monoid]] and combine the results
   * underlying representation of [[Fold]], all [[Fold]] methods are defined in terms of foldMap
   */
  def foldMap[M: Monoid](f: A => M)(s: S): M

  /** combine all targets using a target's [[Monoid]] */
  @inline final def fold(s: S)(implicit ev: Monoid[A]): A =
    foldMap(identity)(s)

  /**
   * get all the targets of a [[Fold]]
   * TODO: Shall it return a Stream as there might be an infinite number of targets?
   */
  @inline final def getAll(s: S): IList[A] =
    foldMap(IList(_))(s)

  /** find the first target of a [[Fold]] matching the predicate  */
  @inline final def find(p: A => Boolean)(s: S): Maybe[A] =
    foldMap(a => if(p(a)) just(a).first else empty[A].first)(s).unwrap

  /** get the first target of a [[Fold]] */
  @inline final def headMaybe(s: S): Maybe[A] =
    find(_ => true)(s)

  /** check if at least one target satisfies the predicate */
  @inline final def exist(p: A => Boolean)(s: S): Boolean =
    foldMap(p(_).disjunction)(s).unwrap

  /** check if all targets satisfy the predicate */
  @inline final def all(p: A => Boolean)(s: S): Boolean =
    foldMap(p(_).conjunction)(s).unwrap

  /** join two [[Fold]] with the same target */
  @inline final def sum[S1](other: Fold[S1, A]): Fold[S \/ S1, A] =
    new Fold[S \/ S1, A]{
      def foldMap[M: Monoid](f: A => M)(s: S \/ S1): M =
        s.fold(self.foldMap(f), other.foldMap(f))
    }

  /** alias for sum */
  @inline final def |||[S1](other: Fold[S1, A]): Fold[S \/ S1, A] =
    sum(other)

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
  @inline def ^|->>[B, C, D](other: PTraversal[A, B, C, D]): Fold[S, C] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[B, C, D](other: POptional[A, B, C, D]): Fold[S, C] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[B, C, D](other: PPrism[A, B, C, D]): Fold[S, C] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[B, C, D](other: PLens[A, B, C, D]): Fold[S, C] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[B, C, D](other: PIso[A, B, C, D]): Fold[S, C] =
    composeIso(other)

}

object Fold extends FoldInstances {
  /** create a [[Fold]] from a [[Foldable]] */
  def fromFoldable[F[_]: Foldable, A]: Fold[F[A], A] =
    new Fold[F[A], A] {
      def foldMap[M: Monoid](f: A => M)(s: F[A]): M =
        Foldable[F].foldMap(s)(f)
    }
}


sealed abstract class FoldInstances {
  implicit val foldChoice: Choice[Fold] = new Choice[Fold]{
    def choice[A, B, C](f: => Fold[A, C], g: => Fold[B, C]): Fold[A \/ B, C] =
      f sum g

    def id[A]: Fold[A, A] =
      Iso.id[A].asFold

    def compose[A, B, C](f: Fold[B, C], g: Fold[A, B]): Fold[A, C] =
      g composeFold f
  }
}
