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
abstract class PPrism[S, T, A, B]{ self =>

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
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] = asSetter composeSetter other
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] = asTraversal composeTraversal other
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] = asOptional composeOptional other
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] = asOptional composeOptional other.asOptional
  final def composePrism[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] = new PPrism[S, T, C, D]{
    @inline def _prism[P[_, _]: ProChoice]: Optic[P, S, T, C, D] = self._prism[P] compose other._prism[P]
  }
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): PPrism[S, T, C, D] = composePrism(other.asPrism)

  // Optic transformation
  @inline final def asSetter: PSetter[S, T, A, B] = PSetter[S, T, A, B](modify)
  final def asFold: Fold[S, A] = new Fold[S, A]{
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M = getMaybe(s) map f getOrElse Monoid[M].zero
  }
  final def asTraversal: PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] = self.modifyF(f)(s)
  }
  final def asOptional: POptional[S, T, A, B] = new POptional[S, T, A, B] {
    @inline def _optional[P[_, _]: Step]: Optic[P, S, T, A, B] = _prism[P]
  }

}

object PPrism {
  def apply[S, T, A, B](seta: S => T \/ A)(_reverseGet: B => T): PPrism[S, T, A, B] = new PPrism[S, T, A, B] {
    @inline def _prism[P[_, _] : ProChoice]: Optic[P, S, T, A, B] = pab =>
      Profunctor[P].dimap(ProChoice[P].right[A, B, T](pab))(seta)(_.fold(identity, _reverseGet))
  }

  final def isMatching[S, T, A, B](prism: PPrism[S, T, A, B])(s: S): Boolean =
    prism.getMaybe(s).isJust
}

object Prism {
  @inline def apply[S, A](_getMaybe: S => Maybe[A])(_reverseGet: A => S): Prism[S, A] =
    PPrism{s: S => _getMaybe(s) \/> s}(_reverseGet)
}
