package monocle

import scalaz.Kleisli._
import scalaz.std.list._
import scalaz.{Applicative, Const, Kleisli, Monoid, Reader, Traverse, IList}

/**
 * A Traversal is generalisation of a Lens in a way that it defines a multi foci between
 * S and 0 to many A.
 */
abstract class Traversal[S, T, A, B] { self =>

  def _traversal[F[_]: Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T]

  final def modifyK[F[_]: Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T] = _traversal(f)

  final def getAll(s: S): IList[A] = _traversal[({ type λ[α] = Const[IList[A], α] })#λ](
    Kleisli[({ type λ[α] = Const[IList[A], α] })#λ, A, B](a => Const(IList(a)))
  ).run(s).getConst

  final def modify(f: A => B): S => T = _traversal(Reader(f)).run
  final def set(b: B): S => T = modify(_ => b)


  // Compose
  final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  final def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = asSetter composeSetter other
  final def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = new Traversal[S, T, C, D] {
    def _traversal[F[_] : Applicative](f: Kleisli[F, C, D]): Kleisli[F, S, T] =
      (self._traversal[F] _ compose other._traversal[F])(f)
  }
  final def composeOptional[C, D](other: Optional[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)
  final def composePrism[C, D](other: Prism[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)
  final def composeLens[C, D](other: Lens[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)
  final def composeIso[C, D](other: Iso[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)

  // Optic transformation
  final def asSetter: Setter[S, T, A, B] = Setter[S, T, A, B](modify)
  final def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[M: Monoid](f: A => M)(s: S): M = _traversal[({ type λ[α] = Const[M, α] })#λ](
      Kleisli[({ type λ[α] = Const[M, α] })#λ, A, B](a => Const(f(a)))
    ).run(s).getConst
  }

}

object Traversal {

  def apply[T[_]: Traverse, A, B]: Traversal[T[A], T[B], A, B] = new Traversal[T[A], T[B], A, B] {
    def _traversal[F[_]: Applicative](f: Kleisli[F, A, B]) = Kleisli[F, T[A], T[B]](s =>
      Traverse[T].traverse(s)(f.run)
    )
  }

  def apply2[S, T, A, B](get1: S => A, get2: S => A)(_set: (S, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](f: Kleisli[F, A, B]) = Kleisli[F, S, T]( s =>
      Applicative[F].apply2(f(get1(s)), f(get2(s)))(_set(s, _, _))
    )
  }

  def apply3[S, T, A, B](get1: S => A, get2: S => A, get3: S => A)(_set: (S, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_] : Applicative](f: Kleisli[F, A, B]) = Kleisli[F, S, T](s =>
      Applicative[F].apply3(f(get1(s)), f(get2(s)), f(get3(s)))(_set(s, _, _, _))
    )
  }

  def apply4[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A)(_set: (S, B, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_] : Applicative](f: Kleisli[F, A, B]) = Kleisli[F, S, T](s =>
      Applicative[F].apply4(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)))(_set(s, _, _, _, _))
    )
  }

  def apply5[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A)(_set: (S, B, B, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_] : Applicative](f: Kleisli[F, A, B]) = Kleisli[F, S, T](s =>
      Applicative[F].apply5(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)))(_set(s, _, _, _, _, _))
    )
  }

  def apply6[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A, get6: S => A)(_set: (S, B, B, B, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_] : Applicative](f: Kleisli[F, A, B]) = Kleisli[F, S, T](s =>
      Applicative[F].apply6(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)))(_set(s, _, _, _, _, _, _))
    )
  }

}
