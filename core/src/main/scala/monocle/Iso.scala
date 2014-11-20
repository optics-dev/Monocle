package monocle

import monocle.internal.{Forget, ProChoice, Strong, Tagged}

import scalaz.Isomorphism.{<=>, <~>}
import scalaz.Profunctor.UpStar
import scalaz.std.function._
import scalaz.syntax.tag._
import scalaz.{Applicative, Functor, Monoid, Profunctor}

/**
 * A [[PIso]] defines an isomorphism between types S, A and B, T:
 *
 *              get                           reverse.get
 *     -------------------->             -------------------->
 *   S                       A         T                       B
 *     <--------------------             <--------------------
 *       reverse.reverseGet                   reverseGet
 *
 *
 * In addition, if f and g forms an isomorphism between A and B, i.e. if f . g = id and g . f = id,
 * then a [[PIso]] defines an isomorphism between S and T:
 *
 *
 *     S           T                                   S           T
 *     |           ↑                                   ↑           |
 *     |           |                                   |           |
 * get |           | reverseGet     reverse.reverseGet |           | reverse.get
 *     |           |                                   |           |
 *     ↓     f     |                                   |     g     ↓
 *     A --------> B                                   A <-------- B
 *
 * [[Iso]] is a type alias for [[PIso]] where S = A and T = B:
 *
 * type Iso[S, A] = PIso[S, S, A, A]
 *
 * A [[PIso]] is also a valid [[Getter]], [[Fold]], [[PLens]], [[PPrism]], [[POptional]], [[PTraversal]] and [[PSetter]]
 *
 * @see IsoLaws in monocle-law module
 *
 * @tparam S the source of the [[PIso]]
 * @tparam T the modified source of the [[PIso]]
 * @tparam A the target of the [[PIso]]
 * @tparam B the modified target of the [[PIso]]
 */
abstract class PIso[S, T, A, B] { self =>

  /** underlying representation of [[PIso]], all [[PIso]] methods are defined in terms of _iso */
  def _iso[P[_, _]: Profunctor]: Optic[P, S, T, A, B]

  /** get the target of a [[PIso]] */
  @inline final def get(s: S): A = _iso[Forget[A, ?, ?]].apply(Forget[A, A, B](identity)).runForget(s)

  /** get the source of a [[PIso]] */
  @inline final def reverseGet(b: B): T = _iso[Tagged].apply(Tagged(b)).untagged

  /** reverse a [[PIso]]: the source becomes the target and the target becomes the source */
  @inline final def reverse: PIso[B, A, T, S] = PIso[B, A, T, S](reverseGet)(get)

  /** modify polymorphically the target of a [[PIso]] with a [[Functor]] function */
  @inline final def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
    _iso[UpStar[F, ?, ?]](Profunctor.upStarProfunctor[F])(UpStar[F, A, B](f)).unwrap.apply(s)

  /** modify polymorphically the target of a [[PIso]] with a function */
  @inline final def modify(f: A => B): S => T = _iso[Function1].apply(f)

  /** set polymorphically the target of a [[PIso]] with a value */
  @inline final def set(b: B): S => T = modify(_ => b)

  /**********************************************************/
  /** Compose methods between a [[PIso]] and another Optics */
  /**********************************************************/

  /** compose a [[PIso]] with a [[Fold]] */
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

  /** compose a [[PIso]] with a [[Getter]] */
  @inline final def composeGetter[C](other: Getter[A, C]): Getter[S, C] =
    asGetter composeGetter other

  /** compose a [[PIso]] with a [[PSetter]] */
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter composeSetter other

  /** compose a [[PIso]] with a [[PTraversal]] */
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    asTraversal composeTraversal other

  /** compose a [[PIso]] with a [[POptional]] */
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    asOptional composeOptional other

  /** compose a [[PIso]] with a [[PPrism]] */
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    asPrism composePrism other

  /** compose a [[PIso]] with a [[PLens]] */
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    asLens composeLens other

  /** compose a [[PIso]] with a [[PIso]] */
  final def composeIso[C, D](other: PIso[A, B, C, D]): PIso[S, T, C, D] = new PIso[S, T, C, D]{
    @inline def _iso[P[_, _]: Profunctor]: Optic[P, S, T, C, D] =
      self._iso[P] compose other._iso[P]
  }

  /****************************************************************/
  /** Transformation methods to view a [[PIso]] as another Optics */
  /****************************************************************/

  /** view a [[PIso]] as a [[Fold]] */
  final def asFold: Fold[S, A] = new Fold[S, A]{
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M =
      f(get(s))
  }
  /** view a [[PIso]] as a [[Getter]] */
  @inline final def asGetter: Getter[S, A] =
    Getter[S, A](get)

  /** view a [[PIso]] as a [[Setter]] */
  @inline final def asSetter: PSetter[S, T, A, B] =
    PSetter[S, T, A, B](modify)

  /** view a [[PIso]] as a [[PTraversal]] */
  final def asTraversal: PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PIso]] as a [[POptional]] */
  final def asOptional: POptional[S, T, A, B] =
    new POptional[S, T, A, B] {
      @inline def _optional[P[_, _]: ProChoice: Strong]: Optic[P, S, T, A, B] =
        _iso[P](Strong[P])
    }

  /** view a [[PIso]] as a [[PPrism]] */
  final def asPrism: PPrism[S, T, A, B] =
    new PPrism[S, T, A, B]{
      @inline def _prism[P[_, _]: ProChoice]: Optic[P, S, T, A, B] =
        _iso[P]
    }

  /** view a [[PIso]] as a [[PLens]] */
  final def asLens: PLens[S, T, A, B] = new PLens[S, T, A, B]{
    @inline def _lens[P[_, _] : Strong]: Optic[P, S, T, A, B] =
      _iso[P]
  }

}

object PIso {
  /** create a [[PIso]] using a pair of functions: one to get the target and one to get the source. */
  def apply[S, T, A, B](_get: S => A)(_reverseGet: B => T): PIso[S, T, A, B] = new PIso[S, T, A, B] {
    @inline def _iso[P[_, _]: Profunctor]: Optic[P, S, T, A, B] =
      Profunctor[P].dimap(_)(_get)(_reverseGet)
  }

  /**
   * create a [[PIso]] between any type and itself. id is the zero element of optics composition,
   * for all optics o of type O (e.g. Lens, Iso, Prism, ...):
   * o      composeIso Iso.id == o
   * Iso.id composeO   o        == o (replace composeO by composeLens, composeIso, composePrism, ...)
   */
  @inline def id[S, T]: PIso[S, T, S, T] =
    PIso[S, T, S, T](identity)(identity)

  /** transform an [[scalaz.Isomorphisms.Iso2]] in a [[PIso]] */
  def fromIsoFunctor[F[_], G[_], A, B](isoFunctor: F <~> G): PIso[F[A], F[B], G[A], G[B]] =
    PIso(isoFunctor.to.apply[A])(isoFunctor.from.apply[B])
}

object Iso {
  /** alias for [[PIso]] apply when S = T and A = B */
  @inline def apply[S, A](_get: S => A)(_reverseGet: A => S): Iso[S, A] =
    PIso(_get)(_reverseGet)

  /** alias for [[PIso]] id when S = T and A = B */
  @inline def id[S]: Iso[S, S] = PIso.id[S, S]

  /** transform an [[scalaz.Isomorphisms.Iso]] in a [[Iso]] */
  def fromIsoSet[A, B](isoSet: A <=> B): Iso[A, B] =
    Iso(isoSet.to)(isoSet.from)
}