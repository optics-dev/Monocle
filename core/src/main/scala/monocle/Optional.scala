package monocle

import monocle.internal.{Forget, ProChoice, Strong, HalfMarket}

import scalaz.Maybe._
import scalaz.Profunctor.UpStar
import scalaz.{Applicative, FirstMaybe, Maybe, Monoid, \/}
import scalaz.syntax.tag._

/**
 * A [[POptional]] can be seen as a pair of functions:
 *  getOrModify: S      => T \/ A
 *  set        : (B, S) => T
 *
 * A [[POptional]] could also be defined as a weaker [[PLens]] and
 * weaker [[PPrism]]
 *
 * [[POptional]] stands for Polymorphic Optional as it set and modify methods change
 * a type A to B and S to T.
 * [[Optional]] is a type alias for [[POptional]] restricted to monomoprhic updates:
 *
 * type Optional[S, A] = POptional[S, S, A, A]
 *
 * @see OptionalLaws in monocle-law module
 *
 * @tparam S the source of the [[POptional]]
 * @tparam T the modified source of the [[POptional]]
 * @tparam A the target of the [[POptional]]
 * @tparam B the modified target of the [[POptional]]
 */
abstract class POptional[S, T, A, B] { self =>

  /** underlying representation of [[POptional]], all [[POptional]] methods are defined in terms of _optional */
  def _optional[P[_, _]: ProChoice: Strong]: Optic[P, S, T, A, B]

  /** get the target of a [[PPrism]] or nothing if there is no target */
  @inline final def getMaybe(s: S): Maybe[A] =
    _optional[Forget[FirstMaybe[A], ?, ?]].apply(Forget[FirstMaybe[A], A, B](
      a => Maybe.just(a).first
    )).runForget(s).unwrap

  /**
   * get the target of a [[POptional]] or modify the source in case there is no target.
   * This method is necessary because a [[POptional]] is also a [[PSetter]], so
   * set and modify need to change the type of the source even though there
   * might be no target.
   * In the case of an [[Optional]], the left side of either is the original source.
   */
  @inline final def getOrModify(s: S): T \/ A =
    _optional[HalfMarket[A, ?, ?]].apply(HalfMarket(\/.right)).seta(s)

  /** modify polymorphically the target of a [[POptional]] with an [[Applicative]] function */
  @inline final def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
    _optional[UpStar[F, ?, ?]](ProChoice.upStarProChoice[F], Strong.upStarStrong[F])(UpStar[F, A, B](f)).unwrap.apply(s)

  /** modify polymorphically the target of a [[POptional]] with a function */
  @inline final def modify(f: A => B): S => T =
    _optional[Function1].apply(f)

  /**
   * modify polymorphically the target of a [[POptional]] with a function.
   * return empty if the [[POptional]] is not matching
   */
  @inline final def modifyMaybe(f: A => B): S => Maybe[T] = s =>
    getMaybe(s).map(_ => modify(f)(s))

  /** set polymorphically the target of a [[POptional]] with a value */
  @inline final def set(b: B): S => T =
    modify(_ => b)

  /**
   * set polymorphically the target of a [[POptional]] with a value.
   * return empty if the [[POptional]] is not matching
   */
  @inline final def setMaybe(b: B): S => Maybe[T] =
    modifyMaybe(_ => b)

  /** check if a [[POptional]] has a target */
  @inline def isMatching(s: S): Boolean =
    getMaybe(s).isJust

  /***************************************************************/
  /** Compose methods between a [[POptional]] and another Optics */
  /***************************************************************/

  /** compose a [[POptional]] with a [[Fold]] */
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

  /** compose a [[POptional]] with a [[Getter]] */
  @inline final def composeGetter[C](other: Getter[A, C]): Fold[S, C] =
    asFold composeGetter other

  /** compose a [[POptional]] with a [[PSetter]] */
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter composeSetter other

  /** compose a [[POptional]] with a [[PTraversal]] */
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    asTraversal composeTraversal other

  /** compose a [[POptional]] with a [[POptional]] */
  final def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    new POptional[S, T, C, D] {
      @inline def _optional[P[_, _]: ProChoice: Strong]: Optic[P, S, T, C, D] =
        self._optional[P] compose other._optional[P]
    }

  /** compose a [[POptional]] with a [[PPrism]] */
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    composeOptional(other.asOptional)

  /** compose a [[POptional]] with a [[PLens]] */
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] =
    composeOptional(other.asOptional)

  /** compose a [[POptional]] with a [[PIso]] */
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): POptional[S, T, C, D] =
    composeOptional(other.asOptional)

  /*********************************************************************/
  /** Transformation methods to view a [[POptional]] as another Optics */
  /*********************************************************************/

  /** view a [[POptional]] as a [[Fold]] */
  final def asFold: Fold[S, A] = new Fold[S, A]{
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M =
      getMaybe(s) map f getOrElse Monoid[M].zero
  }

  /** view a [[POptional]] as a [[PSetter]] */
  @inline final def asSetter: PSetter[S, T, A, B] =
    PSetter[S, T, A, B](modify)

  /** view a [[POptional]] as a [[PTraversal]] */
  final def asTraversal: PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      self.modifyF(f)(s)
  }

}

object POptional {
  def apply[S, T, A, B](seta: S => T \/ A)(_set: (B, S) => T): POptional[S, T, A, B] = new POptional[S, T, A, B] {
    @inline def _optional[P[_, _]: ProChoice: Strong]: Optic[P, S, T, A, B] = pab =>
      Strong[P].dimap(ProChoice[P].right(Strong[P].first(pab)): P[T \/ (A, S), T \/ (B, S)]){s: S => seta(s).map((_, s))}(_.fold(identity, _set.tupled))
  }
}

object Optional {
  @inline def apply[S, A](_getMaybe: S => Maybe[A])(_set: (A, S) => S): Optional[S, A] =
    POptional{s: S => _getMaybe(s) \/> s}( _set)
}
