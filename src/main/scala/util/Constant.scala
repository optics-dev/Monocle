package util

import scalaz.Functor


case class Constant[A, B](value: A)

object Constant {
  implicit def ConstantFunctor[T] = new Functor[({type l[a] = Constant[T,a]})#l] {
    def map[A, B](fa: Constant[T, A])(f: A => B): Constant[T, B] = Constant[T,B](fa.value)
  }
}
