package monocle

import _root_.scalaz.Functor

trait Setter[S, T, A, B] { self =>

  def set(from: S, newValue: B): T = modify(from, _ => newValue)

  final def setF(newValue: B): S => T = set(_, newValue)

  def modify(from: S, f: A => B): T

  final def modifyF(f: A => B): S => T = modify(_, f)

  def asSetter: Setter[S, T, A, B] = self

  /** non overloaded compose function */
  def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = compose(other)

  def compose[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = new Setter[S, T, C, D] {
    def modify(from: S, f: C => D): T = self.modify(from, other.modify(_, f))
  }

}

object Setter {

  def apply[F[_]: Functor, A, B]: Setter[F[A], F[B], A, B] = new Setter[F[A], F[B], A, B] {
    def modify(from: F[A], f: A => B): F[B] = Functor[F].map(from)(f)
  }

}
