package lens.util

import scalaz.Functor

case class Identity[A](value: A)

object Identity {
  implicit object IdentityFunctor extends Functor[Identity] {
    def map[A, B](fa: Identity[A])(f: A => B): Identity[B] = Identity(f(fa.value))
  }
}
