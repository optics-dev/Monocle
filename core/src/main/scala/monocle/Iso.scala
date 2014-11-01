package monocle

import monocle.internal.{Forget, ProChoice, Step, Strong, Tagged}

import scalaz.Profunctor.UpStar
import scalaz.std.function._
import scalaz.{Applicative, Functor, Monoid, Profunctor, Tag}
import scalaz.Isomorphism.{<~>, <=>}

/**
 * An Iso is a Lens that can be reversed and so it defines an isomorphism.
 */
abstract class PIso[S, T, A, B] { self =>

  /** underlying representation of [[PIso]], all [[PIso]] methods are defined in terms of _iso */
  def _iso[P[_, _]: Profunctor]: Optic[P, S, T, A, B]

  /** get the target of [[PIso]] */
  @inline final def get(s: S): A = _iso[Forget[A, ?, ?]].apply(Forget[A, A, B](identity)).runForget(s)

  /** get the source of [[PIso]] */
  @inline final def reverseGet(b: B): T = _iso[Tagged].apply(Tagged(b)).untagged

  /** reverse a [[PIso]]: the source becomes the target and the target becomes the source */
  @inline final def reverse: PIso[B, A, T, S] = PIso[B, A, T, S](reverseGet)(get)

  /** modify polymorphically the target of a [[PIso]] using [[Functor]] function */
  @inline final def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
    Tag.unwrap(_iso[UpStar[F, ?, ?]](Profunctor.upStarProfunctor[F])(UpStar[F, A, B](f))).apply(s)

  /** modify polymorphically the target of a [[PIso]] using a function */
  @inline final def modify(f: A => B): S => T = _iso[Function1].apply(f)

  /** set polymorphically the target of a [[PIso]] with a value */
  @inline final def set(b: B): S => T = modify(_ => b)

  /************************************************************************************************/
  /** Compose methods between a [[PLens]] and another Optics                                      */
  /************************************************************************************************/

  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  @inline final def composeGetter[C](other: Getter[A, C]): Getter[S, C] = asGetter composeGetter other
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] = asSetter composeSetter other
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] = asTraversal composeTraversal other
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] = asOptional composeOptional other
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] = asPrism composePrism other
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] = asLens composeLens other
  final def composeIso[C, D](other: PIso[A, B, C, D]): PIso[S, T, C, D] = new PIso[S, T, C, D]{
    @inline def _iso[P[_, _]: Profunctor]: Optic[P, S, T, C, D] = self._iso[P] compose other._iso[P]
  }

  /************************************************************************************************/
  /** Transformation methods to view a [[PIso]] as another Optics                                 */
  /************************************************************************************************/

  @inline final def asSetter: PSetter[S, T, A, B] = PSetter[S, T, A, B](modify)
  final def asFold: Fold[S, A] = new Fold[S, A]{
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M = f(get(s))
  }
  final def asTraversal: PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] = self.modifyF(f)(s)
  }
  final def asOptional: POptional[S, T, A, B] = new POptional[S, T, A, B] {
    @inline def _optional[P[_, _]: Step]: Optic[P, S, T, A, B] = _iso[P]
  }
  final def asPrism: PPrism[S, T, A, B] = new PPrism[S, T, A, B]{
    @inline def _prism[P[_, _]: ProChoice]: Optic[P, S, T, A, B] = _iso[P]
  }
  @inline final def asGetter: Getter[S, A] = Getter[S, A](get)
  final def asLens: PLens[S, T, A, B] = new PLens[S, T, A, B]{
    @inline def _lens[P[_, _] : Strong]: Optic[P, S, T, A, B] = _iso[P]
  }

}

object PIso {
  def apply[S, T, A, B](_get: S => A)(_reverseGet: B => T): PIso[S, T, A, B] = new PIso[S, T, A, B] {
    @inline def _iso[P[_, _]: Profunctor]: Optic[P, S, T, A, B] =
      Profunctor[P].dimap(_)(_get)(_reverseGet)
  }

  def fromIsoFunctor[F[_], G[_], A, B](isoFunctor: F <~> G): PIso[F[A], F[B], G[A], G[B]] =
    PIso(isoFunctor.to.apply[A])(isoFunctor.from.apply[B])
}

object Iso {
  @inline def apply[S, A](_get: S => A)(_reverseGet: A => S): Iso[S, A] =
    PIso(_get)(_reverseGet)

  @inline def dummy[S]: Iso[S, S] =
    Iso[S, S](identity)(identity)

  def fromIsoSet[A, B](isoSet: A <=> B): Iso[A, B] =
    Iso(isoSet.to)(isoSet.from)
}