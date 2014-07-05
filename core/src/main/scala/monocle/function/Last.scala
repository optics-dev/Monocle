package monocle.function

import monocle.SimpleLens
import monocle.function.Fields._

trait Last[S, A] {

  /**
   * Creates a Lens from S to its last element
   * last is safe, it should only be implemented on object with a last element
   */
  def last: SimpleLens[S, A]

}


object Last extends LastInstances

trait LastInstances {

  def last[S, A](implicit ev: Last[S, A]): SimpleLens[S, A] = ev.last

  implicit def tuple2Last[A1, A2] = new Last[(A1, A2), A2] {
    def last = second
  }

  implicit def tuple3Last[A1, A2, A3] = new Last[(A1, A2, A3), A3] {
    def last = third
  }

  implicit def tuple4Last[A1, A2, A3, A4] = new Last[(A1, A2, A3, A4), A4] {
    def last = fourth
  }

  implicit def tuple5Last[A1, A2, A3, A4, A5] = new Last[(A1, A2, A3, A4, A5), A5] {
    def last = fifth
  }

  implicit def tuple6Last[A1, A2, A3, A4, A5, A6] = new Last[(A1, A2, A3, A4, A5, A6), A6] {
    def last = sixth
  }

}