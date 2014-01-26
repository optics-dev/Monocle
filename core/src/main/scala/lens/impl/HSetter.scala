package lens.impl

import scalaz.Functor
import lens.Setter


class HSetter[F[_] : Functor, A] extends Setter[F[A], A] {
  def modify(from: F[A], f: A => A): F[A] = Functor[F].map(from)(f)
}

object HSetter {
  def apply[F[_] : Functor, A]: Setter[F[A], A] = new HSetter[F, A]
}
