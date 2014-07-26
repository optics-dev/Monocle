package monocle

import _root_.scalaz.Id._
import _root_.scalaz.std.list._
import _root_.scalaz.{ Const, Monoid, Traverse, Applicative }

/**
 * A Traversal is generalisation of a Lens in a way that it defines a multi foci between
 * S and 0 to many A.
 */
trait Traversal[S, T, A, B] extends Setter[S, T, A, B] with Fold[S, A] { self =>

  def multiLift[F[_]: Applicative](from: S, f: A => F[B]): F[T]

  def modify(from: S, f: A => B): T = multiLift[Id](from, { a: A => id.point(f(a)) })

  def foldMap[M: Monoid](from: S)(f: A => M): M =
    multiLift[({ type l[a] = Const[M, a] })#l](from, { a: A => Const[M, B](f(a)) }).getConst

  def asTraversal: Traversal[S, T, A, B] = self

  /** non overloaded compose function */
  def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = new Traversal[S, T, C, D] {
    def multiLift[F[_]: Applicative](from: S, f: C => F[D]): F[T] = self.multiLift(from, other.multiLift(_, f))
  }

  @deprecated("Use composeTraversal", since = "0.5")
  def compose[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other)

}

object Traversal {

  def apply[T[_]: Traverse, A, B]: Traversal[T[A], T[B], A, B] = new Traversal[T[A], T[B], A, B] {
    def multiLift[F[_]: Applicative](from: T[A], f: A => F[B]): F[T[B]] = Traverse[T].traverse(from)(f)
  }

  def apply2[S, T, A, B](get1: S => A, get2: S => A)(_set: (S, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def multiLift[F[_]: Applicative](from: S, f: A => F[B]): F[T] =
      Applicative[F].apply2(f(get1(from)), f(get2(from)))((v1, v2) => _set(from, v1, v2))
  }

  def apply3[S, T, A, B](get1: S => A, get2: S => A, get3: S => A)(_set: (S, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def multiLift[F[_]: Applicative](from: S, f: A => F[B]): F[T] =
      Applicative[F].apply3(f(get1(from)), f(get2(from)), f(get3(from)))((v1, v2, v3) => _set(from, v1, v2, v3))
  }

  def apply4[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A)(_set: (S, B, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def multiLift[F[_]: Applicative](from: S, f: A => F[B]): F[T] =
      Applicative[F].apply4(f(get1(from)), f(get2(from)), f(get3(from)), f(get4(from)))((v1, v2, v3, v4) => _set(from, v1, v2, v3, v4))
  }

  def apply5[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A)(_set: (S, B, B, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def multiLift[F[_]: Applicative](from: S, f: A => F[B]): F[T] =
      Applicative[F].apply5(f(get1(from)), f(get2(from)), f(get3(from)), f(get4(from)), f(get5(from)))((v1, v2, v3, v4, v5) => _set(from, v1, v2, v3, v4, v5))
  }

  def apply6[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A, get6: S => A)(_set: (S, B, B, B, B, B, B) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def multiLift[F[_]: Applicative](from: S, f: A => F[B]): F[T] =
      Applicative[F].apply6(f(get1(from)), f(get2(from)), f(get3(from)), f(get4(from)), f(get5(from)), f(get6(from)))((v1, v2, v3, v4, v5, v6) => _set(from, v1, v2, v3, v4, v5, v6))
  }

}
