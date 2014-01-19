package lens.util

import scalaz.{Applicative, Monoid, Functor}


case class Constant[A, B](value: A)

object Constant {
  implicit def ConstantFunctor[T] = new Functor[({type l[a] = Constant[T,a]})#l] {
    def map[A, B](fa: Constant[T, A])(f: A => B): Constant[T, B] = Constant[T,B](fa.value)
  }

  implicit def ConstantApplicative[T : Monoid] = new Applicative[({type l[a] = Constant[T,a]})#l] {
    val M = implicitly[Monoid[T]]

    def point[A](a: => A): Constant[T,A] = Constant[T,A](M.zero)

    def ap[A, B](fa: => Constant[T,A])(f: => Constant[T,A => B]): Constant[T,B] =
      Constant[T, B](M.append(fa.value, f.value))
  }
}
