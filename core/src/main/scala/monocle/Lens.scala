package monocle

import monocle.util.Constant
import monocle.util.Constant._
import scalaz.{ Applicative, Functor, Monoid }

/**
 * A Lens defines a single focus between a type S and A such as if you change A to B
 * you obtain a T.
 */
trait Lens[S, T, A, B] extends Traversal[S, T, A, B] with Getter[S, A] { self =>

  def lift[F[_]: Functor](from: S, f: A => F[B]): F[T]

  def multiLift[F[_]: Applicative](from: S, f: A => F[B]): F[T] = lift(from, f)

  override def foldMap[M: Monoid](from: S)(f: A => M): M = lift[({ type l[a] = Constant[M, a] })#l](from, { a: A => Constant.apply[M, A](f(a)) })

  def get(from: S): A = lift[({ type l[b] = Constant[A, b] })#l](from, { a: A => Constant.apply[A, B](a) })

  /** non overloaded compose function */
  def composeLens[C, D](other: Lens[A, B, C, D]): Lens[S, T, C, D] = compose(other)

  def compose[C, D](other: Lens[A, B, C, D]): Lens[S, T, C, D] = new Lens[S, T, C, D] {
    def lift[F[_]: Functor](from: S, f: C => F[D]): F[T] = self.lift(from, other.lift(_, f))
  }
}

object Lens {

  def apply[S, T, A, B](_get: S => A, _set: (S, B) => T): Lens[S, T, A, B] = new Lens[S, T, A, B] {
    def lift[F[_]: Functor](from: S, f: A => F[B]): F[T] =
      Functor[F].map(f(_get(from)))(newValue => _set(from, newValue))
  }

}
