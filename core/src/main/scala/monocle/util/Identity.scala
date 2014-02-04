package monocle.util

import scalaz.Applicative

case class Identity[A](value: A)

object Identity {

  implicit object IdentityApplicative extends Applicative[Identity] {
    def point[A](a: => A): Identity[A] = Identity(a)
    def ap[A, B](fa: => Identity[A])(f: => Identity[(A) => B]): Identity[B] = Identity(f.value(fa.value))
  }
}
