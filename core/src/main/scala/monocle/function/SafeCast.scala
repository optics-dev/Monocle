package monocle.function

import monocle.SimplePrism
import monocle.internal.Bounded


trait SafeCast[S, A] {
  
  def safeCast: SimplePrism[S, A]

}

object SafeCast extends SafeCastFunctions

trait SafeCastFunctions {
  
  def safeCast[S, A](implicit ev: SafeCast[S, A]): SimplePrism[S, A] = ev.safeCast

  def orderingBoundedSafeCast[S: Ordering, A: Bounded](revCast: A => S, unsafeCast: S => A): SafeCast[S, A] = new SafeCast[S, A] {
    def safeCast = SimplePrism[S, A](revCast, { from: S =>
      val ord = implicitly[Ordering[S]]
      if (ord.gt(from, revCast(Bounded[A].MaxValue)) ||
          ord.lt(from, revCast(Bounded[A].MinValue))) None else Some(unsafeCast(from))
    })
  }

}

