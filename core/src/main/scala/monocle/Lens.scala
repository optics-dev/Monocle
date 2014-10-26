package monocle

import monocle.internal.{ Forget, Step, Strong }

import scalaz.Profunctor.UpStar
import scalaz.{ Applicative, Functor, Monoid, Profunctor, Tag }

/**
 * A [[Lens]] is a purely functional concept permitting to view and update polymorphically an *A*
 * from an *S*
 *
 * a [[Lens]] is also a valid [[Getter]], [[Fold]], [[Optional]], [[Traversal]] and [[Setter]]
 */
abstract class Lens[S, T, A, B] { self =>

  /**
   * underlying representation of a [[Lens]], all [[Lens]] methods are defined in terms of _lens
   */
  def _lens[P[_, _]: Strong]: Optic[P, S, T, A, B]

  /** get the target of [[Lens]] */
  @inline final def get(s: S): A = _lens[Forget[A, ?, ?]].apply(Forget(identity)).runForget(s)

  /**
   * modify polymorphically the target of a [[Lens]] using [[Functor]] function
   */
  @inline final def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
    Tag.unwrap(_lens[UpStar[F, ?, ?]](Strong.upStarStrong[F])(UpStar[F, A, B](f))).apply(s)

  /** modify polymorphically the target of a [[Lens]] using a function */
  @inline final def modify(f: A => B): S => T = _lens[Function1].apply(f)

  /** set polymorphically the target of a [[Lens]] with a value */
  @inline final def set(b: B): S => T = modify(_ => b)

  /************************************************************************************************/
  /** Compose methods between a [[Lens]] and another Optics                                       */
  /************************************************************************************************/

  /** compose a [[Lens]] with a [[Fold]] */
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  /** compose a [[Lens]] with a [[Getter]] */
  @inline final def composeGetter[C](other: Getter[A, C]): Getter[S, C] = asGetter composeGetter other
  /** compose a [[Lens]] with a [[Setter]] */
  @inline final def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = asSetter composeSetter other
  /** compose a [[Lens]] with a [[Traversal]] */
  @inline final def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = asTraversal composeTraversal other
  /** compose a [[Lens]] with an [[Optional]] */
  @inline final def composeOptional[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = asOptional composeOptional other
  /** compose a [[Lens]] with a [[Prism]] */
  @inline final def composePrism[C, D](other: Prism[A, B, C, D]): Optional[S, T, C, D] = asOptional composeOptional other.asOptional
  /** compose a [[Lens]] with a [[Lens]] */
  final def composeLens[C, D](other: Lens[A, B, C, D]): Lens[S, T, C, D] = new Lens[S, T, C, D] {
    @inline def _lens[P[_, _]: Strong]: Optic[P, S, T, C, D] = self._lens[P] compose other._lens[P]
  }
  /** compose a [[Lens]] with an [[Iso]] */
  final def composeIso[C, D](other: Iso[A, B, C, D]): Lens[S, T, C, D] = composeLens(other.asLens)

  /************************************************************************************************/
  /** Transformation methods to view a [[Lens]] as another Optics                                 */
  /************************************************************************************************/

  /** view a [[Lens]] as a [[Fold]] */
  final def asFold: Fold[S, A] = new Fold[S, A] {
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M = f(get(s))
  }
  /** view a [[Lens]] as a [[Getter]] */
  @inline final def asGetter: Getter[S, A] = Getter[S, A](get)
  /** view a [[Lens]] as a [[Setter]] */
  @inline final def asSetter: Setter[S, T, A, B] = Setter[S, T, A, B](modify)
  /** view a [[Lens]] as a [[Traversal]] */
  final def asTraversal: Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] = self.modifyF(f)(s)
  }
  /** view a [[Lens]] as an [[Optional]] */
  final def asOptional: Optional[S, T, A, B] = new Optional[S, T, A, B] {
    @inline final def _optional[P[_, _]: Step]: Optic[P, S, T, A, B] = _lens[P]
  }

}

object Lens {

  /**
   * create a [[Lens]] using a pair of functions: one to get the target, one to set the target.
   * @see macro module to see [[Lens]] methods generating less boiler plate
   */
  def apply[S, T, A, B](_get: S => A)(_set: (B, S) => T): Lens[S, T, A, B] = new Lens[S, T, A, B] {
    @inline final def _lens[P[_, _]: Strong]: Optic[P, S, T, A, B] = pab =>
      Profunctor[P].dimap[(A, S), (B, S), S, T](Strong[P].first[A, B, S](pab))(s => (_get(s), s))(_set.tupled)
  }

}
