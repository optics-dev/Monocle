package monocle

import monocle.internal.{Forget, Step}

import scalaz.Maybe._
import scalaz.Profunctor.UpStar
import scalaz.{Applicative, FirstMaybe, Maybe, Monoid, Profunctor, Tag, \/}

/**
 * Optional can be seen as a partial Lens - Lens toward an Option - or
 * a 0-1 Traversal. The latter constraint is not enforce at compile time
 * but by OptionalLaws
 */
abstract class POptional[S, T, A, B] { self =>

  def _optional[P[_, _]: Step]: Optic[P, S, T, A, B]

  @inline final def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
    Tag.unwrap(_optional[UpStar[F, ?, ?]](Step.upStarStep[F])(UpStar[F, A, B](f))).apply(s)

  @inline final def getMaybe(s: S): Maybe[A] = Tag.unwrap(
    _optional[Forget[FirstMaybe[A], ?, ?]].apply(Forget[FirstMaybe[A], A, B](
      a => Maybe.just(a).first
    )).runForget(s)
  )

  @inline final def modify(f: A => B): S => T = _optional[Function1].apply(f)
  @inline final def modifyMaybe(f: A => B): S => Maybe[T] = s => getMaybe(s).map(_ => modify(f)(s))

  @inline final def set(b: B): S => T = modify(_ => b)
  @inline final def setMaybe(b: B): S => Maybe[T] = modifyMaybe(_ => b)

  // Compose
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] = asSetter composeSetter other
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] = asTraversal composeTraversal other
  final def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] = new POptional[S, T, C, D] {
    @inline def _optional[P[_, _]: Step]: Optic[P, S, T, C, D] = self._optional[P] compose other._optional[P]
  }
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] = composeOptional(other.asOptional)
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] = composeOptional(other.asOptional)
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): POptional[S, T, C, D] = composeOptional(other.asOptional)

  // Optic transformation
  final def asFold: Fold[S, A] = new Fold[S, A]{
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M = getMaybe(s) map f getOrElse Monoid[M].zero
  }
  @inline final def asSetter: PSetter[S, T, A, B] = PSetter[S, T, A, B](modify)
  final def asTraversal: PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] = self.modifyF(f)(s)
  }

}

object POptional {
  def apply[S, T, A, B](seta: S => T \/ A)(_set: (B, S) => T): POptional[S, T, A, B] = new POptional[S, T, A, B] {
    @inline def _optional[P[_, _]: Step]: Optic[P, S, T, A, B] = pab =>
      Profunctor[P].dimap(Step[P].step[A, B, T, S](pab)){s: S => seta(s).map((_, s))}(_.fold(identity, _set.tupled))
  }
}

object Optional {
  @inline def apply[S, A](_getMaybe: S => Maybe[A])(_set: (A, S) => S): Optional[S, A] =
    POptional{s: S => _getMaybe(s) \/> s}( _set)
}
