package monocle

import scalaz.Id._
import scalaz.std.list._
import scalaz.{ Const, Monoid, Traverse, Applicative }

/**
 * A Traversal is generalisation of a Lens in a way that it defines a multi foci between
 * S and 0 to many A.
 */
trait Traversal[S, T, A, B] extends Setter[S, T, A, B] with Fold[S, A] { self =>

  def _traversal[F[_]: Applicative](s: S, f: A => F[B]): F[T]

  final def multiLift[F[_]: Applicative](s: S, f: A => F[B]): F[T] = _traversal(s, f)

  final def modify(s: S, f: A => B): T = multiLift[Id](s, { a: A => id.point(f(a)) })

  final def foldMap[M: Monoid](s: S)(f: A => M): M =
    multiLift[({ type l[a] = Const[M, a] })#l](s, { a: A => Const[M, B](f(a)) }).getConst

  final def asTraversal: Traversal[S, T, A, B] = self

  /** non overloaded compose function */
  final def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = new Traversal[S, T, C, D] {
    def _traversal[F[_]: Applicative](s: S, f: C => F[D]): F[T] = self.multiLift(s, other.multiLift(_, f))
  }

  @deprecated("Use composeTraversal", since = "0.5")
  def compose[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other)

}

object Traversal {

  def apply[T[_]: Traverse, A, B]: Traversal[T[A], T[B], A, B] = new Traversal[T[A], T[B], A, B] {
    def _traversal[F[_]: Applicative](s: T[A], f: A => F[B]): F[T[B]] = Traverse[T].traverse(s)(f)
  }

  def apply2[S, T, A, B](get1: S => A, get2: S => A)(_set: (S, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](s: S, f: A => F[B]): F[T] =
      Applicative[F].apply2(f(get1(s)), f(get2(s)))((v1, v2) => _set(s, v1, v2))
  }

  def apply3[S, T, A, B](get1: S => A, get2: S => A, get3: S => A)(_set: (S, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](s: S, f: A => F[B]): F[T] =
      Applicative[F].apply3(f(get1(s)), f(get2(s)), f(get3(s)))((v1, v2, v3) => _set(s, v1, v2, v3))
  }

  def apply4[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A)(_set: (S, B, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](s: S, f: A => F[B]): F[T] =
      Applicative[F].apply4(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)))((v1, v2, v3, v4) => _set(s, v1, v2, v3, v4))
  }

  def apply5[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A)(_set: (S, B, B, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](s: S, f: A => F[B]): F[T] =
      Applicative[F].apply5(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)))((v1, v2, v3, v4, v5) => _set(s, v1, v2, v3, v4, v5))
  }

  def apply6[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A, get6: S => A)(_set: (S, B, B, B, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[F[_]: Applicative](s: S, f: A => F[B]): F[T] =
      Applicative[F].apply6(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)))((v1, v2, v3, v4, v5, v6) => _set(s, v1, v2, v3, v4, v5, v6))
  }

}
