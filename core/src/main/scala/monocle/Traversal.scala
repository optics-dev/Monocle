package monocle

import scalaz.Id.Id
import scalaz.{Applicative, Const, IList, Monoid, Traverse}


/**
 * A Traversal is generalisation of a Lens in a way that it defines a multi foci between
 * S and 0 to many A.
 */
abstract class PTraversal[S, T, A, B] { self =>

  def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T]

  @inline final def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] = _traversal(f)(s)

  @inline final def getAll(s: S): IList[A] = _traversal[Const[IList[A], ?]](
    a => Const(IList(a))
  )(s).getConst

  @inline final def modify(f: A => B): S => T = _traversal[Id](f)
  @inline final def set(b: B): S => T = modify(_ => b)

  // Compose
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] = asSetter composeSetter other
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] = new PTraversal[S, T, C, D] {
    @inline def _traversal[F[_]: Applicative](f: C => F[D])(s: S): F[T] =
      self._traversal(other._traversal(f)(_))(s)
  }
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): PTraversal[S, T, C, D] = composeTraversal(other.asTraversal)
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): PTraversal[S, T, C, D] = composeTraversal(other.asTraversal)
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): PTraversal[S, T, C, D] = composeTraversal(other.asTraversal)
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): PTraversal[S, T, C, D] = composeTraversal(other.asTraversal)

  // Optic transformation
  @inline final def asSetter: PSetter[S, T, A, B] = PSetter[S, T, A, B](modify)
  final def asFold: Fold[S, A] = new Fold[S, A]{
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M =
      _traversal[Const[M, ?]](a => Const(f(a)))(s).getConst
  }

}

object PTraversal {

  def apply[T[_]: Traverse, A, B]: PTraversal[T[A], T[B], A, B] = new PTraversal[T[A], T[B], A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: T[A]): F[T[B]] = Traverse[T].traverse(s)(f)
  }

  def apply2[S, T, A, B](get1: S => A, get2: S => A)(_set: (B, B, S) => T): PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      Applicative[F].apply2(f(get1(s)), f(get2(s)))(_set(_, _, s))
  }

  def apply3[S, T, A, B](get1: S => A, get2: S => A, get3: S => A)(_set: (B, B, B, S) => T): PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      Applicative[F].apply3(f(get1(s)), f(get2(s)), f(get3(s)))(_set(_, _, _, s))
  }

  def apply4[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A)(_set: (B, B, B, B, S) => T): PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      Applicative[F].apply4(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)))(_set(_, _, _, _, s))
  }

  def apply5[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A)(_set: (B, B, B, B, B, S) => T): PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      Applicative[F].apply5(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)))(_set(_, _, _, _, _, s))
  }

  def apply6[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A, get6: S => A)(_set: (B, B, B, B, B, B, S) => T): PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def _traversal[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      Applicative[F].apply6(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)))(_set(_, _, _, _, _, _, s))
  }

}
