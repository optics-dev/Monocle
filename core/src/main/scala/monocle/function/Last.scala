package monocle.function

import monocle.SimpleLens
import monocle.syntax.lens._

trait Last[S, A] {

  /**
   * Creates a Lens from S to its last element
   * last is safe, it should only be implemented on object with a last element
   */
  def last: SimpleLens[S, A]

}


object Last extends LastInstances

trait LastInstances extends LastInstances1 {

  def last[S, A](implicit ev: Last[S, A]): SimpleLens[S, A] = ev.last

}

trait LastInstances1 {

  implicit def reverseFirstLast[S, RS, A](implicit evReverse: Reverse[S, RS], evField1: Field1[RS, A]): Last[S, A] = new Last[S, A]{
    def last: SimpleLens[S, A] = evReverse.reverse |-> evField1._1
  }

}
