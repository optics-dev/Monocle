package monocle

import monocle.internal.{Forget, ProChoice, Step, Strong, Tagged}

import scalaz.Profunctor.UpStar
import scalaz.std.function._
import scalaz.{Applicative, Functor, Monoid, Profunctor, Tag}
import scalaz.Isomorphism.{<~>, <=>}

/**
 * An Iso is a Lens that can be reversed and so it defines an isomorphism.
 */
abstract class Iso[S, T, A, B] { self =>

  def _iso[P[_, _]: Profunctor]: Optic[P, S, T, A, B]

  @inline final def reverse: Iso[B, A, T, S] = Iso[B, A, T, S](reverseGet)(get)

  @inline final def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
    Tag.unwrap(_iso[UpStar[F, ?, ?]](Profunctor.upStarProfunctor[F])(UpStar[F, A, B](f))).apply(s)

  @inline final def get(s: S): A = _iso[Forget[A, ?, ?]].apply(Forget[A, A, B](identity)).runForget(s)
  @inline final def reverseGet(b: B): T = _iso[Tagged].apply(Tagged(b)).untagged

  @inline final def modify(f: A => B): S => T = _iso[Function1].apply(f)
  @inline final def set(b: B): S => T = modify(_ => b)


  // Compose
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  @inline final def composeGetter[C](other: Getter[A, C]): Getter[S, C] = asGetter composeGetter other
  @inline final def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = asSetter composeSetter other
  @inline final def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = asTraversal composeTraversal other
  @inline final def composeOptional[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = asOptional composeOptional other
  @inline final def composePrism[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = asPrism composePrism other
  @inline final def composeLens[C, D](other: Lens[A, B, C, D]): Lens[S, T, C, D] = asLens composeLens other
  final def composeIso[C, D](other: Iso[A, B, C, D]): Iso[S, T, C, D] = new Iso[S, T, C, D]{
    @inline def _iso[P[_, _]: Profunctor]: Optic[P, S, T, C, D] = self._iso[P] compose other._iso[P]
  }


  // Optics transformation
  @inline final def asSetter: Setter[S, T, A, B] = Setter[S, T, A, B](modify)
  final def asFold: Fold[S, A] = new Fold[S, A]{
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M = f(get(s))
  }
  final def asTraversal: Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] = self.modifyF(f)(s)
  }
  final def asOptional: Optional[S, T, A, B] = new Optional[S, T, A, B] {
    @inline def _optional[P[_, _]: Step]: Optic[P, S, T, A, B] = _iso[P]
  }
  final def asPrism: Prism[S, T, A, B] = new Prism[S, T, A, B]{
    @inline def _prism[P[_, _]: ProChoice]: Optic[P, S, T, A, B] = _iso[P]
  }
  @inline final def asGetter: Getter[S, A] = Getter[S, A](get)
  final def asLens: Lens[S, T, A, B] = new Lens[S, T, A, B]{
    @inline def _lens[P[_, _] : Strong]: Optic[P, S, T, A, B] = _iso[P]
  }

}

object Iso {

  def apply[S, T, A, B](_get: S => A)(_reverseGet: B => T): Iso[S, T, A, B] = new Iso[S, T, A, B] {
    @inline def _iso[P[_, _]: Profunctor]: Optic[P, S, T, A, B] =
      Profunctor[P].dimap(_)(_get)(_reverseGet)
  }

  def fromIsoFunctor[F[_], G[_], A, B](isoFunctor: F <~> G): Iso[F[A], F[B], G[A], G[B]] =
    Iso(isoFunctor.to.apply[A])(isoFunctor.from.apply[B])

  def fromIsoSet[A, B](isoSet: A <=> B): SimpleIso[A, B] =
    SimpleIso(isoSet.to)(isoSet.from)

}
