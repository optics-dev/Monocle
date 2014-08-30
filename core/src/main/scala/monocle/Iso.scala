package monocle

import monocle.internal.{Tagged, Strong, ProChoice}

import scalaz.{Applicative, Functor, Profunctor, Monoid, Const, Kleisli}
import scalaz.std.function._
import scalaz.Id.Id

/**
 * An Iso is a Lens that can be reversed and so it defines an isomorphism.
 */
abstract class Iso[S, T, A, B] { self =>

  def _iso[P[_, _]: Profunctor, F[_]: Functor](pafb: P[A, F[B]]): P[S, F[T]]

  final def reverse: Iso[B, A, T, S] = Iso[B, A, T, S](reverseGet, get)

  final def modifyK[F[_]: Functor](f: Kleisli[F, A, B]): Kleisli[F, S, T] =
    Kleisli[F, S, T](_iso[Function1, F](f.run))

  final def get(s: S): A = _iso[Function1, ({ type λ[α] = Const[A, α] })#λ](a => Const(a)).apply(s).getConst
  final def reverseGet(b: B): T = _iso[Tagged, Id](Tagged(b)).untagged

  final def modify(f: A => B): S => T = _iso[Function1, Id](f)
  final def set(b: B): S => T = modify(_ => b)


  // Compose
  final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  final def composeGetter[C](other: Getter[A, C]): Getter[S, C] = asGetter composeGetter other
  final def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = asSetter composeSetter other
  final def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = asTraversal composeTraversal other
  final def composeOptional[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = asOptional composeOptional other
  final def composePrism[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = asPrism composePrism other
  final def composeLens[C, D](other: Lens[A, B, C, D]): Lens[S, T, C, D] = asLens composeLens other
  final def composeIso[C, D](other: Iso[A, B, C, D]): Iso[S, T, C, D] = new Iso[S, T, C, D]{
    def _iso[P[_, _]: Profunctor, F[_]: Functor](pcfd: P[C, F[D]]): P[S, F[T]] =
      (self._iso[P, F] _ compose other._iso[P, F])(pcfd)
  }


  // Optics transformation
  final def asSetter: Setter[S, T, A, B] = Setter[S, T, A, B](modify)
  final def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[M: Monoid](s: S)(f: A => M): M =
      _iso[Function1, ({ type λ[α] = Const[M, α] })#λ](a => Const(f(a))).apply(s).getConst
  }
  final def asTraversal: Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_] : Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T] = self.modifyK(f)
  }
  final def asOptional: Optional[S, T, A, B] = new Optional[S, T, A, B] {
    def _optional[F[_] : Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T] = self.modifyK(f)
  }
  final def asPrism: Prism[S, T, A, B] = new Prism[S, T, A, B]{
    def _prism[P[_, _]: ProChoice, F[_]: Applicative](pafb: P[A, F[B]]): P[S, F[T]] = _iso(pafb)
  }
  final def asGetter: Getter[S, A] = Getter[S, A](get)
  final def asLens: Lens[S, T, A, B] = new Lens[S, T, A, B]{
    def _lens[P[_, _] : Strong](pab: P[A, B]): P[S, T] = _iso[P, Id](pab)
  }

}

object Iso {

  def apply[S, T, A, B](_get: S => A, _reverseGet: B => T): Iso[S, T, A, B] = new Iso[S, T, A, B] {
    def _iso[P[_, _], F[_]](pafb: P[A, F[B]])(implicit p: Profunctor[P], f: Functor[F]): P[S, F[T]] =
      p.mapsnd(p.mapfst(pafb)(_get))(f.map(_)(_reverseGet))
  }

}
