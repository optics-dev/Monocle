package lens

import scalaz.Functor


trait Setter[A, B] {

  def set(from: A, newValue: B): A  = modify(from, _ => newValue)

  def modify(from: A, f: B => B): A

  def >-[C](other: Setter[B,C]): Setter[A,C] = Setter.compose(this, other)

}

object Setter {

  def apply[F[_] : Functor, A]: Setter[F[A], A] = new Setter[F[A], A] {
    def modify(from: F[A], f: A => A): F[A] = Functor[F].map(from)(f)
  }

  def compose[A, B, C](a2B: Setter[A, B], b2C: Setter[B, C]): Setter[A, C] = new Setter[A, C] {
    def modify(from: A, f: C => C): A = a2B.modify(from, b2C.modify(_, f))
  }
}







