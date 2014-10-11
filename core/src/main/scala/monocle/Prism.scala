package monocle

import monocle.internal.{Forget, ProChoice, Step, Tagged}

import scalaz.Maybe._
import scalaz.Profunctor.UpStar
import scalaz.{ Maybe, Applicative, FirstMaybe, Tag, Monoid, Profunctor, \/}


/**
 * A Prism is a special case of Traversal where the focus is limited to
 * 0 or 1 A. In addition, a Prism defines a reverse relation such as
 * you can always get T from B.
 */
abstract class Prism[S, T, A, B]{ self =>

  def _prism[P[_, _]: ProChoice]: Optic[P, S, T, A, B]

  @inline final def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
    Tag.unwrap(_prism[UpStar[F, ?, ?]](ProChoice.upStarProChoice[F])(UpStar[F, A, B](f))).apply(s)

  @inline final def getMaybe(s: S): Maybe[A] = Tag.unwrap(
    _prism[Forget[FirstMaybe[A], ?, ?]].apply(Forget[FirstMaybe[A], A, B](
      a => Maybe.just(a).first
    )).runForget(s)
  )

  @inline final def reverseGet(b: B): T = _prism[Tagged].apply(Tagged(b)).untagged
  @inline final def re: Getter[B, T] = Getter(reverseGet)

  @inline final def modify(f: A => B): S => T = _prism[Function1].apply(f)
  @inline final def modifyMaybe(f: A => B): S => Maybe[T] = s => getMaybe(s).map(_ => modify(f)(s))

  @inline final def set(b: B): S => T = modify(_ => b)
  @inline final def setMaybe(b: B): S => Maybe[T] = modifyMaybe(_ => b)

  // Compose
  final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  final def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = asSetter composeSetter other
  final def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = asTraversal composeTraversal other
  final def composeOptional[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = asOptional composeOptional other
  final def composeLens[C, D](other: Lens[A, B, C, D]): Optional[S, T, C, D] = asOptional composeOptional other.asOptional
  final def composePrism[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = new Prism[S, T, C, D]{
    @inline def _prism[P[_, _]: ProChoice]: Optic[P, S, T, C, D] = self._prism[P] compose other._prism[P]
  }
  final def composeIso[C, D](other: Iso[A, B, C, D]): Prism[S, T, C, D] = composePrism(other.asPrism)

  // Optic transformation
  final def asSetter: Setter[S, T, A, B] = Setter[S, T, A, B](modify)
  final def asFold: Fold[S, A] = new Fold[S, A]{
    final def foldMap[M: Monoid](f: A => M)(s: S): M = getMaybe(s) map f getOrElse Monoid[M].zero
  }
  final def asTraversal: Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    final def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] = self.modifyF(f)(s)
  }
  final def asOptional: Optional[S, T, A, B] = new Optional[S, T, A, B] {
    final def _optional[P[_, _]: Step]: Optic[P, S, T, A, B] = _prism[P]
  }

}

object Prism extends PrismFunctions {

  def apply[S, T, A, B](seta: S => T \/ A)(_reverseGet: B => T): Prism[S, T, A, B] = new Prism[S, T, A, B] {
    @inline def _prism[P[_, _] : ProChoice]: Optic[P, S, T, A, B] = pab =>
      Profunctor[P].dimap(ProChoice[P].right[A, B, T](pab))(seta)(_.fold(identity, _reverseGet))
  }

}

trait PrismFunctions {
  final def isMatching[S, T, A, B](prism: Prism[S, T, A, B])(s: S): Boolean =
    prism.getMaybe(s).isJust
}
