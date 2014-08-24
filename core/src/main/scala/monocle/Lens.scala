package monocle

import scalaz.{Applicative, Const, Functor}

/**
 * A Lens defines a single focus between a type S and A such as if you change A to B
 * you obtain a T.
 */
trait Lens[S, T, A, B] extends Optional[S, T, A, B] with Getter[S, A] { self =>

  def _lens[F[_]: Functor](s: S, f: A => F[B]): F[T]

  def lift[F[_]: Functor](s: S, f: A => F[B]): F[T] = _lens(s, f)

  def _traversal[F[_]: Applicative](from: S, f: A => F[B]): F[T] = lift(from, f)


  def get(from: S): A = lift[({ type l[b] = Const[A, b] })#l](from, { a: A => Const[A, B](a) }).getConst

  /** non overloaded compose function */
  def composeLens[C, D](other: Lens[A, B, C, D]): Lens[S, T, C, D] = new Lens[S, T, C, D] {
    def _lens[F[_]: Functor](from: S, f: C => F[D]): F[T] = self.lift(from, other.lift(_, f))
  }

  @deprecated("Use composeLens", since = "0.5")
  def compose[C, D](other: Lens[A, B, C, D]): Lens[S, T, C, D] = composeLens(other)
}

object Lens {

  def apply[S, T, A, B](_get: S => A, _set: (S, B) => T): Lens[S, T, A, B] = new Lens[S, T, A, B] {
    def _lens[F[_]: Functor](from: S, f: A => F[B]): F[T] =
      Functor[F].map(f(_get(from)))(newValue => _set(from, newValue))
  }

}
