package monocle

import scalaz.Id._
import scalaz._

/**
 * Optional can be seen as a partial Lens - Lens toward an Option - or
 * a 0-1 Traversal. The latter constraint is not enforce at compile time
 * but by OptionalLaws
 */
abstract class Optional[S, T, A, B] { self =>

  def _optional[F[_]: Applicative](s: S, f: A => F[B]): F[T]

  final def getMaybe(s: S): Maybe[A] = asFold.headMaybe(s)

  final def modifyF(f: A => B): S => T = _optional[Id](_, a => id.point(f(a)))
  final def modify(s: S, f: A => B): T = modifyF(f)(s)
  final def modifyMaybeF(f: A => B): S => Maybe[T] = modifyMaybe(_, f)
  final def modifyMaybe(s: S, f: A => B): Maybe[T] = getMaybe(s).map(a => set(s, f(a)))

  final def setF(newValue: B): S => T = modifyF(_ => newValue)
  final def set(s: S, newValue: B): T = setF(newValue)(s)
  final def setMaybeF(newValue: B): S => Maybe[T] = modifyMaybeF(_ => newValue)
  final def setMaybe(s: S, newValue: B): Maybe[T] = setMaybeF(newValue)(s)

  // Compose
  final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  final def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = asSetter composeSetter other
  final def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = asTraversal composeTraversal other
  final def composeOptional[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = new Optional[S, T, C, D] {
    def _optional[F[_] : Applicative](s: S, f: C => F[D]): F[T] = self._optional(s, other._optional(_, f))
  }
  final def composePrism[C, D](other: Prism[A, B, C, D]): Optional[S, T, C, D] = composeOptional(other.asOptional)
  final def composeLens[C, D](other: Lens[A, B, C, D]): Optional[S, T, C, D] = composeOptional(other.asOptional)
  final def composeIso[C, D](other: Iso[A, B, C, D]): Optional[S, T, C, D] = composeOptional(other.asOptional)

  // Optic transformation
  final def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[M: Monoid](s: S)(f: A => M): M =
      _optional[({ type l[a] = Const[M, a] })#l](s, a => Const[M, B](f(a))).getConst
  }
  final def asSetter: Setter[S, T, A, B] = Setter[S, T, A, B](modifyF)
  final def asTraversal: Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_] : Applicative](s: S, f: A => F[B]): F[T] = _optional(s, f)
  }

}

object Optional {

  def apply[S, T, A, B](seta: S => T \/ A, _set: (S, B) => T): Optional[S, T, A, B] = new Optional[S, T, A, B] {
    def _optional[F[_] : Applicative](s: S, f: A => F[B]): F[T] =
      seta(s)                                   // T    \/ A
        .map(f)                                 // T    \/ F[B]
        .map(Applicative[F].map(_)(_set(s, _))) // T    \/ F[T]
        .leftMap(Applicative[F].point(_))       // F[T] \/ F[T]
        .fold(identity, identity)               // F[T]
  }

}
