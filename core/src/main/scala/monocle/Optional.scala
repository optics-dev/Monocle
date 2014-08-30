package monocle

import scalaz.Maybe._
import scalaz.{Applicative, Const, FirstMaybe, Kleisli, Maybe, Monoid, Reader, Tag, \/}

/**
 * Optional can be seen as a partial Lens - Lens toward an Option - or
 * a 0-1 Traversal. The latter constraint is not enforce at compile time
 * but by OptionalLaws
 */
abstract class Optional[S, T, A, B] { self =>

  def _optional[F[_]: Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T]

  final def modifyK[F[_]: Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T] = _optional(f)

  final def getMaybe(s: S): Maybe[A] =
    Tag.unwrap(
      _optional[({ type λ[α] = Const[FirstMaybe[A], α] })#λ](
        Kleisli[({ type λ[α] = Const[FirstMaybe[A], α] })#λ, A, B](
         a => Const(Maybe.just(a).first)
        )
      ).run(s).getConst
    )

  final def modify(f: A => B): S => T = _optional(Reader(f)).run
  final def modifyMaybe(f: A => B): S => Maybe[T] = s => getMaybe(s).map(_ => modify(f)(s))

  final def set(b: B): S => T = modify(_ => b)
  final def setMaybe(b: B): S => Maybe[T] = modifyMaybe(_ => b)

  // Compose
  final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  final def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = asSetter composeSetter other
  final def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = asTraversal composeTraversal other
  final def composeOptional[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = new Optional[S, T, C, D] {
    def _optional[F[_] : Applicative](f: Kleisli[F, C, D]): Kleisli[F, S, T] =
      (self._optional[F] _ compose other._optional[F])(f)
  }
  final def composePrism[C, D](other: Prism[A, B, C, D]): Optional[S, T, C, D] = composeOptional(other.asOptional)
  final def composeLens[C, D](other: Lens[A, B, C, D]): Optional[S, T, C, D] = composeOptional(other.asOptional)
  final def composeIso[C, D](other: Iso[A, B, C, D]): Optional[S, T, C, D] = composeOptional(other.asOptional)

  // Optic transformation
  final def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[M: Monoid](s: S)(f: A => M): M = getMaybe(s) map f getOrElse Monoid[M].zero
  }
  final def asSetter: Setter[S, T, A, B] = Setter[S, T, A, B](modify)
  final def asTraversal: Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_] : Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T] = _optional(f)
  }

}

object Optional {

  def apply[S, T, A, B](seta: S => T \/ A, _set: (S, B) => T): Optional[S, T, A, B] = new Optional[S, T, A, B] {
    def _optional[F[_] : Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T] =
      Kleisli[F, S, T]( s =>
        seta(s)                                   // T    \/ A
          .map(f)                                 // T    \/ F[B]
          .map(Applicative[F].map(_)(_set(s, _))) // T    \/ F[T]
          .leftMap(Applicative[F].point(_))       // F[T] \/ F[T]
          .fold(identity, identity)               // F[T]
      )

  }

}
