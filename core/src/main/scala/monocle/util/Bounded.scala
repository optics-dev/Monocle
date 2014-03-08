package monocle.util

import monocle.SimplePrism

trait Bounded[T] {
  def MinValue: T
  def MaxValue: T
}

object Bounded {
  def apply[T](implicit ev: Bounded[T]): Bounded[T] = ev

  def safeCast[A: Ordering, B: Bounded](revCast: B => A, unsafeCast: A => B): SimplePrism[A, B] =
    SimplePrism(revCast, { a: A =>
      val ord = implicitly[Ordering[A]]
      if (ord.gt(a, revCast(Bounded[B].MaxValue)) || ord.lt(a, revCast(Bounded[B].MinValue))) None else Some(unsafeCast(a))
    })

}