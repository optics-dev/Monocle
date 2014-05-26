package monocle.internal

import scalaz.{ Applicative, Monoid, Functor }

private[monocle] object Constant {

  type Constant[A, B] = A

  def apply[A, B](a: A): Constant[A, B] = a

  implicit def ConstantFunctor[T] = new Functor[({ type l[a] = Constant[T, a] })#l] {
    def map[A, B](fa: Constant[T, A])(f: A => B): Constant[T, B] = fa
  }

  implicit def ConstantApplicative[T: Monoid] = new Applicative[({ type l[a] = Constant[T, a] })#l] {
    def point[A](a: => A): Constant[T, A] = Monoid[T].zero

    def ap[A, B](fa: => Constant[T, A])(f: => Constant[T, A => B]): Constant[T, B] =
      Monoid[T].append(fa, f)
  }
}
