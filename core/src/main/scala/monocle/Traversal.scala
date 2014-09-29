package monocle

import scalaz.Id.Id
import scalaz.{Applicative, Const, IList, Monoid, Traverse}


/**
 * A Traversal is generalisation of a Lens in a way that it defines a multi foci between
 * S and 0 to many A.
 */
abstract class Traversal[S, T, A, B] { self =>

  def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T]

  // TODO use def _traversal[P[_, _]: Walk]: Optic[P, S, T, A, B]

  final def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] = _traversal(f)(s)

  final def getAll(s: S): IList[A] = modifyF[Const[IList[A], ?]](
    a => Const(IList(a))
  )(s).getConst

  final def modify(f: A => B): S => T = _traversal[Id](f)
  final def set(b: B): S => T = modify(_ => b)

  // Compose
  final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  final def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = asSetter composeSetter other
  final def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = new Traversal[S, T, C, D] {
    def _traversal[F[_]: Applicative](f: C => F[D])(s: S): F[T] =
      self._traversal(other._traversal(f)(_))(s)
  }
  final def composeOptional[C, D](other: Optional[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)
  final def composePrism[C, D](other: Prism[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)
  final def composeLens[C, D](other: Lens[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)
  final def composeIso[C, D](other: Iso[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)

  // Optic transformation
  final def asSetter: Setter[S, T, A, B] = Setter[S, T, A, B](modify)
  final def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[M: Monoid](f: A => M)(s: S): M =
      modifyF[Const[M, ?]](a => Const(f(a)))(s).getConst
  }

}

object Traversal {

  def apply[T[_]: Traverse, A, B]: Traversal[T[A], T[B], A, B] = new Traversal[T[A], T[B], A, B] {
    def _traversal[F[_]: Applicative](f: A => F[B])(s: T[A]): F[T[B]] = Traverse[T].traverse(s)(f)
  }

  def apply2[S, T, A, B](get1: S => A, get2: S => A)(_set: (B, B, S) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      Applicative[F].apply2(f(get1(s)), f(get2(s)))(_set(_, _, s))
  }

  def apply3[S, T, A, B](get1: S => A, get2: S => A, get3: S => A)(_set: (B, B, B, S) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      Applicative[F].apply3(f(get1(s)), f(get2(s)), f(get3(s)))(_set(_, _, _, s))
  }

  def apply4[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A)(_set: (B, B, B, B, S) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      Applicative[F].apply4(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)))(_set(_, _, _, _, s))
  }

  def apply5[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A)(_set: (B, B, B, B, B, S) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      Applicative[F].apply5(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)))(_set(_, _, _, _, _, s))
  }

  def apply6[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A, get6: S => A)(_set: (B, B, B, B, B, B, S) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      Applicative[F].apply6(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)))(_set(_, _, _, _, _, _, s))
  }

}
