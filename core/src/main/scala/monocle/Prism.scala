package monocle

import monocle.internal.{ProChoice, Tagged}

import scalaz.Id.Id
import scalaz.Maybe._
import scalaz.{Monoid, Applicative, \/, Kleisli, Maybe, FirstMaybe, Const, Tag}

/**
 * A Prism is a special case of Traversal where the focus is limited to
 * 0 or 1 A. In addition, a Prism defines a reverse relation such as
 * you can always get T from B.
 */
abstract class Prism[S, T, A, B]{ self =>

  def _prism[P[_, _]: ProChoice, F[_]: Applicative](pafb: P[A, F[B]]): P[S, F[T]]

  final def modifyK[F[_]: Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T] =
    Kleisli[F, S, T](_prism[Function1, F](f.run))

  final def getMaybe(s: S): Maybe[A] = Tag.unwrap(
    _prism[Function1, ({ type λ[α] = Const[FirstMaybe[A], α] })#λ](
      a => Const(Maybe.just(a).first)
    ).apply(s).getConst
  )

  final def reverseGet(b: B): T = _prism[Tagged, Id](Tagged(b)).untagged
  final def re: Getter[B, T] = Getter(reverseGet)

  final def modify(f: A => B): S => T = _prism[Function1, Id](f)
  final def modifyMaybe(f: A => B): S => Maybe[T] = s => getMaybe(s).map(_ => modify(f)(s))

  final def set(b: B): S => T = modify(_ => b)
  final def setMaybe(b: B): S => Maybe[T] = modifyMaybe(_ => b)

  // Compose
  final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  final def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = asSetter composeSetter other
  final def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = asTraversal composeTraversal other
  final def composeOptional[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = asOptional composeOptional other
  final def composeLens[C, D](other: Lens[A, B, C, D]): Optional[S, T, C, D] = asOptional composeOptional other.asOptional
  final def composePrism[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = new Prism[S, T, C, D]{
    def _prism[P[_, _]: ProChoice, F[_]: Applicative](pcfd: P[C, F[D]]): P[S, F[T]] =
      (self._prism[P, F] _ compose other._prism[P, F])(pcfd)
  }
  final def composeIso[C, D](other: Iso[A, B, C, D]): Prism[S, T, C, D] = composePrism(other.asPrism)

  // Optic transformation
  final def asSetter: Setter[S, T, A, B] = Setter[S, T, A, B](_prism[Function1, Id])
  final def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[M: Monoid](f: A => M)(s: S): M = getMaybe(s) map f getOrElse Monoid[M].zero
  }
  final def asTraversal: Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T] = self.modifyK(f)
  }
  final def asOptional: Optional[S, T, A, B] = new Optional[S, T, A, B] {
    def _optional[F[_] : Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T] = self.modifyK(f)
  }

}

object Prism extends PrismFunctions {

  def apply[S, T, A, B](seta: S => T \/ A, _reverseGet: B => T): Prism[S, T, A, B] = new Prism[S, T, A, B] {
    def _prism[P[_, _], F[_]](pafb: P[A, F[B]])(implicit p: ProChoice[P], f: Applicative[F]): P[S, F[T]] =
      p.mapsnd(p.mapfst[T \/ A, T \/ F[B], S](p.right(pafb))(seta))(_.fold(f.point[T](_), f.map(_)(_reverseGet)))
  }

}

trait PrismFunctions {
  final def isMatching[S, T, A, B](prism: Prism[S, T, A, B])(s: S): Boolean =
    prism.getMaybe(s).isJust
}
