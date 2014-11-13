package monocle

import monocle.internal.{Forget, ProChoice, Step, Tagged, Market}

import scalaz.Maybe._
import scalaz.Profunctor.UpStar
import scalaz.{ Maybe, Applicative, FirstMaybe, Monoid, Profunctor, \/}
import scalaz.syntax.tag._


/**
 * A [[PPrism]] can be seen as a pair of functions:
 *  getOrModify: S => T \/ A
 *  reverseGet : B => T
 *
 * A [[PPrism]] could also be defined as a weaker [[PIso]] where get can fail.
 *
 * Typically a [[PPrism]] or [[Prism]] encodes the relation between a Sum or
 * CoProduct type (e.g. sealed trait) and one of it is element.
 *
 * [[Prism]] is a type alias for [[PPrism]] where the type of target cannot be modified:
 *
 * type Prism[S, A] = PPrism[S, S, A, A]
 *
 * A [[PPrism]] is also a valid  [[Fold]], [[POptional]], [[PTraversal]] and [[PSetter]]
 *
 * @see PrismLaws in monocle-law module
 *
 * @tparam S the source of the [[PPrism]]
 * @tparam T the modified source of the [[PPrism]]
 * @tparam A the target of the [[PPrism]]
 * @tparam B the modified target of the [[PPrism]]
 */
abstract class PPrism[S, T, A, B]{ self =>

  /** underlying representation of [[PPrism]], all [[PPrism]] methods are defined in terms of _prism */
  def _prism[P[_, _]: ProChoice]: Optic[P, S, T, A, B]

  /** get the target of a [[PPrism]] or nothing if there is no target */
  @inline final def getMaybe(s: S): Maybe[A] =
    _prism[Forget[FirstMaybe[A], ?, ?]].apply(
      Forget(a => Maybe.just(a).first)
    ).runForget(s).unwrap

  /**
   * get the target of a [[PPrism]] or modify the source in case there is no target.
   * It is necessary to modify the source when the [[PPrism]] is not matching,
   * otherwise we could not implement set and modify methods which require to  This method is necessary because a [[PPrism]] is also a [[PSetter]], so
   * set and modify need to change the type of the source even though there
   * might be no target.
   *
   * In the case of a [[Prism]], the left side of either is the original source.
   */
  @inline final def getOrModify(s: S): T \/ A =
    _prism[Market[A, B, ?, ?]].apply(Market(\/.right, identity)).getOr(s)


  /** get the modified source of a [[PPrism]] */
  @inline final def reverseGet(b: B): T =
    _prism[Tagged].apply(Tagged(b)).untagged

  /** create a [[Getter]] from the modified target to the modified source of a [[PPrism]] */
  @inline final def re: Getter[B, T] =
    Getter(reverseGet)

  /** modify polymorphically the target of a [[PPrism]] with an [[Applicative]] function */
  @inline final def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
    _prism[UpStar[F, ?, ?]](ProChoice.upStarProChoice[F])(UpStar[F, A, B](f)).unwrap.apply(s)

  /**
   * modify polymorphically the target of a [[PPrism]] with a function
   */
  @inline final def modify(f: A => B): S => T =
    _prism[Function1].apply(f)

  /**
   * modify polymorphically the target of a [[PPrism]] with a function.
   * return empty if the [[PPrism]] is not matching
   */
  @inline final def modifyMaybe(f: A => B): S => Maybe[T] =
    s => getMaybe(s).map(_ => modify(f)(s))

  /** set polymorphically the target of a [[PIso]] with a value */
  @inline final def set(b: B): S => T =
    modify(_ => b)

  /**
   * set polymorphically the target of a [[PPrism]] with a value.
   * return empty if the [[PPrism]] is not matching
   */
  @inline final def setMaybe(b: B): S => Maybe[T] =
    modifyMaybe(_ => b)

  /** check if a [[PPrism]] has a target */
  @inline def isMatching(s: S): Boolean =
    getMaybe(s).isJust

  /************************************************************************************************/
  /** Compose methods between a [[PPrism]] and another Optics                                     */
  /************************************************************************************************/

  /** compose a [[PPrism]] with a [[Fold]] */
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

  /** compose a [[PPrism]] with a [[PSetter]] */
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter composeSetter other

  /** compose a [[PPrism]] with a [[PTraversal]] */
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    asTraversal composeTraversal other

  /** compose a [[PPrism]] with a [[POptional]] */
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    asOptional composeOptional other

  /** compose a [[PPrism]] with a [[PLens]] */
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] =
    asOptional composeOptional other.asOptional

  /** compose a [[PPrism]] with a [[PPrism]] */
  final def composePrism[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] = new PPrism[S, T, C, D]{
    @inline def _prism[P[_, _]: ProChoice]: Optic[P, S, T, C, D] = self._prism[P] compose other._prism[P]
  }

  /** compose a [[PPrism]] with a [[PIso]] */
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): PPrism[S, T, C, D] =
    composePrism(other.asPrism)

  /************************************************************************************************/
  /** Transformation methods to view a [[PPrism]] as another Optics                               */
  /************************************************************************************************/

  /** view a [[PPrism]] as a [[Fold]] */
  final def asFold: Fold[S, A] = new Fold[S, A]{
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M = getMaybe(s) map f getOrElse Monoid[M].zero
  }

  /** view a [[PPrism]] as a [[Setter]] */
  @inline final def asSetter: PSetter[S, T, A, B] =
    PSetter[S, T, A, B](modify)

  /** view a [[PPrism]] as a [[PTraversal]] */
  final def asTraversal: PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] = self.modifyF(f)(s)
  }

  /** view a [[PPrism]] as a [[POptional]] */
  final def asOptional: POptional[S, T, A, B] = new POptional[S, T, A, B] {
    @inline def _optional[P[_, _]: Step]: Optic[P, S, T, A, B] = _prism[P]
  }

}

object PPrism {
  /** create a [[PPrism]] using the canonical functions: getOrModify and reverseGet */
  def apply[S, T, A, B](_getOrModify: S => T \/ A)(_reverseGet: B => T): PPrism[S, T, A, B] =
    new PPrism[S, T, A, B] {
      @inline def _prism[P[_, _] : ProChoice]: Optic[P, S, T, A, B] = pab =>
        Profunctor[P].dimap(ProChoice[P].right[A, B, T](pab))(_getOrModify)(_.fold(identity, _reverseGet))
    }

}

object Prism {
  /** alias for [[PPrism]] apply restricted to monomorphic update */
  @inline def apply[S, A](_getMaybe: S => Maybe[A])(_reverseGet: A => S): Prism[S, A] =
    PPrism{s: S => _getMaybe(s) \/> s}(_reverseGet)
}
