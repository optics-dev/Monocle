package monocle

import scalaz.Functor

trait Setter[S, T, A, B] { self =>

  def modifyF(f: A => B): S => T
  final def modify(s: S, f: A => B): T = modifyF(f)(s)

  final def set(s: S, newValue: B): T = modify(s, _ => newValue)
  final def setF(newValue: B): S => T = set(_, newValue)

  final def asSetter: Setter[S, T, A, B] = self

  /** non overloaded compose function */
  final def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] =
    Setter[S, T, C, D](self.modifyF _ compose other.modifyF)

  @deprecated("Use composeSetter", since = "0.5")
  def compose[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = composeSetter(other)

}

object Setter {

  def apply[S, T, A, B](_modifyF: (A => B) => (S => T)): Setter[S, T, A, B] = new Setter[S, T, A, B] {
    def modifyF(f: A => B): S => T = _modifyF(f)
  }

  def apply[F[_]: Functor, A, B]: Setter[F[A], F[B], A, B] = new Setter[F[A], F[B], A, B] {
    def modifyF(f: A => B): F[A] => F[B] = Functor[F].map(_)(f)
  }

}
