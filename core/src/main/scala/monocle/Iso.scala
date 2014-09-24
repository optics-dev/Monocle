package monocle

import monocle.internal.{ProChoice, Step, Strong, Tagged}

import scalaz.std.function._
import scalaz.{Applicative, Const, Functor, Kleisli, Monoid, Profunctor}

/**
 * An Iso is a Lens that can be reversed and so it defines an isomorphism.
 */
abstract class Iso[S, T, A, B] { self =>

  def _iso[P[_, _]: Profunctor]: Optic[P, S, T, A, B]

  final def reverse: Iso[B, A, T, S] = Iso[B, A, T, S](reverseGet, get)

  final def modifyK[F[_]: Functor](f: Kleisli[F, A, B]): Kleisli[F, S, T] =
    _iso[Kleisli[F, ?, ?]].apply(f)

  final def get(s: S): A = modifyK[Const[A, ?]](
    Kleisli[Const[A, ?], A, B](a => Const(a))
  ).run(s).getConst
  final def reverseGet(b: B): T = _iso[Tagged].apply(Tagged(b)).untagged

  final def modify(f: A => B): S => T = _iso[Function1].apply(f)
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
    def _iso[P[_, _]: Profunctor]: Optic[P, S, T, C, D] = self._iso[P] compose other._iso[P]
  }


  // Optics transformation
  final def asSetter: Setter[S, T, A, B] = Setter[S, T, A, B](modify)
  final def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[M: Monoid](f: A => M)(s: S): M = f(get(s))
  }
  final def asTraversal: Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T] = self.modifyK(f)
  }
  final def asOptional: Optional[S, T, A, B] = new Optional[S, T, A, B] {
    def _optional[P[_, _]: Step]: Optic[P, S, T, A, B] = _iso[P]
  }
  final def asPrism: Prism[S, T, A, B] = new Prism[S, T, A, B]{
    def _prism[P[_, _]: ProChoice]: Optic[P, S, T, A, B] = _iso[P]
  }
  final def asGetter: Getter[S, A] = Getter[S, A](get)
  final def asLens: Lens[S, T, A, B] = new Lens[S, T, A, B]{
    def _lens[P[_, _] : Strong]: Optic[P, S, T, A, B] = _iso[P]
  }

}

object Iso {

  def apply[S, T, A, B](_get: S => A, _reverseGet: B => T): Iso[S, T, A, B] = new Iso[S, T, A, B] {
    def _iso[P[_, _]: Profunctor]: Optic[P, S, T, A, B] =
      Profunctor[P].dimap(_)(_get)(_reverseGet)
  }

}
