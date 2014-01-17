package haskell

import scalaz.Functor


trait Lens[A, B] {
  protected def lensFunction[F[_] : Functor](b2Fb: B => F[B], a: A): F[A]

  def set(a: A, b: B): A = {
    val b2Fb: B => Identity[B] = { _: B => Identity.apply(b) }
    lensFunction[Identity](b2Fb, a).a
  }
  def get(a: A): B = ???
}




