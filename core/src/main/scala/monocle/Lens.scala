package monocle

import monocle.internal.{ Forget, Step, Strong }

import scalaz.Profunctor.UpStar
import scalaz.{ Applicative, Functor, Monoid, Profunctor, Tag }
import scalaz.syntax.tag._

/**
 * A [[PLens]] defines a purely functional relation between a type S and an smaller type A.
 * A [[PLens]] can be seen as a pair of function get and set defined as follow:
 *  get: S      => A  i.e. from an S, we can extract an A
 *  set: (B, S) => T  i.e. if we replace an A by a B in a S, we obtain a T
 *
 * [[PLens]] stands for Polymorphic Lens as it set and modify methods change a type A to B and S to T.
 * [[Lens]] is a type alias for [[PLens]] restricted to monomoprhic updates:
 *
 * type Lens[S, A] = PLens[S, S, A, A]
 *
 * A [[PLens]] is also a valid [[Getter]], [[Fold]], [[POptional]], [[PTraversal]] and [[PSetter]]
 *
 * Typically a [[PLens]] or [[Lens]] can be defined between a [[Product]] (e.g. case class, tuple, HList)
 * and one of it is component.
 */
abstract class PLens[S, T, A, B] { self =>

  /** underlying representation of [[PLens]], all [[PLens]] methods are defined in terms of _lens */
  def _lens[P[_, _]: Strong]: Optic[P, S, T, A, B]

  /** get the target of [[PLens]] */
  @inline final def get(s: S): A = _lens[Forget[A, ?, ?]].apply(Forget(identity)).runForget(s)

  /** modify polymorphically the target of a [[PLens]] using [[Functor]] function */
  @inline final def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
    _lens[UpStar[F, ?, ?]](Strong.upStarStrong[F])(UpStar[F, A, B](f)).unwrap.apply(s)

  /** modify polymorphically the target of a [[PLens]] using a function */
  @inline final def modify(f: A => B): S => T = _lens[Function1].apply(f)

  /** set polymorphically the target of a [[PLens]] with a value */
  @inline final def set(b: B): S => T = modify(_ => b)

  /************************************************************************************************/
  /** Compose methods between a [[PLens]] and another Optics                                      */
  /************************************************************************************************/

  /** compose a [[PLens]] with a [[Fold]] */
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  /** compose a [[PLens]] with a [[Getter]] */
  @inline final def composeGetter[C](other: Getter[A, C]): Getter[S, C] = asGetter composeGetter other
  /** compose a [[PLens]] with a [[PSetter]] */
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] = asSetter composeSetter other
  /** compose a [[PLens]] with a [[PTraversal]] */
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] = asTraversal composeTraversal other
  /** compose a [[PLens]] with an [[POptional]] */
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] = asOptional composeOptional other
  /** compose a [[PLens]] with a [[PPrism]] */
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] = asOptional composeOptional other.asOptional
  /** compose a [[PLens]] with a [[PLens]] */
  final def composeLens[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] = new PLens[S, T, C, D] {
    @inline def _lens[P[_, _]: Strong]: Optic[P, S, T, C, D] = self._lens[P] compose other._lens[P]
  }
  /** compose a [[PLens]] with an [[PIso]] */
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): PLens[S, T, C, D] = composeLens(other.asLens)

  /************************************************************************************************/
  /** Transformation methods to view a [[PLens]] as another Optics                                */
  /************************************************************************************************/

  /** view a [[PLens]] as a [[Fold]] */
  final def asFold: Fold[S, A] = new Fold[S, A] {
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M = f(get(s))
  }
  /** view a [[PLens]] as a [[Getter]] */
  final def asGetter: Getter[S, A] = Getter[S, A](get)
  /** view a [[PLens]] as a [[PSetter]] */
  final def asSetter: PSetter[S, T, A, B] = PSetter[S, T, A, B](modify)
  /** view a [[PLens]] as a [[PTraversal]] */
  final def asTraversal: PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] = self.modifyF(f)(s)
  }
  /** view a [[PLens]] as an [[POptional]] */
  final def asOptional: POptional[S, T, A, B] = new POptional[S, T, A, B] {
    @inline final def _optional[P[_, _]: Step]: Optic[P, S, T, A, B] = _lens[P]
  }

}

object PLens {
  /**
   * create a [[PLens]] using a pair of functions: one to get the target, one to set the target.
   * @see macro module for methods generating [[PLens]] with less boiler plate
   */
  def apply[S, T, A, B](_get: S => A)(_set: (B, S) => T): PLens[S, T, A, B] = new PLens[S, T, A, B] {
    @inline final def _lens[P[_, _]: Strong]: Optic[P, S, T, A, B] = pab =>
      Profunctor[P].dimap[(A, S), (B, S), S, T](Strong[P].first[A, B, S](pab))(s => (_get(s), s))(_set.tupled)
  }

}

object Lens {
  /** alias for [[PLens]] apply with a monomorphic set function */
  @inline def apply[S, A](_get: S => A)(_set: (A, S) => S): Lens[S, A] =
    PLens(_get)(_set)
}