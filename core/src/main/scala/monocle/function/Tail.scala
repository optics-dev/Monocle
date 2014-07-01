package monocle.function

import monocle.SimpleLens
import scalaz.OneAnd

trait Tail[S, A] {

  /**
   * Creates an Lens between S and its tail A.
   * tail is strictly stronger than tailOption as the presence of a
   * tail for S is mandatory
   */
  def tail: SimpleLens[S, A]

}

object Tail extends TailInstances

trait TailInstances {

  def tail[S, A](implicit ev: Tail[S, A]): SimpleLens[S, A] = ev.tail

  implicit def oneAndTail[T[_], A] = new Tail[OneAnd[T, A], T[A]]{
    def tail = SimpleLens[OneAnd[T, A], T[A]](_.tail, (oneAnd, tail) => oneAnd.copy(tail = tail))
  }

  implicit def tuple2Tail[A1, A2] = new Tail[(A1, A2), A2] {
    def tail = SimpleLens[(A1, A2), A2](_._2, (t, a) => t.copy(_2 = a))
  }

  implicit def tuple3Tail[A1, A2, A3] = new Tail[(A1, A2, A3), (A2, A3)] {
    def tail = SimpleLens[(A1, A2, A3), (A2, A3)](t => (t._2, t._3), (t, a) => t.copy(_2 = a._1, _3 = a._2))
  }

  implicit def tuple4Tail[A1, A2, A3, A4] = new Tail[(A1, A2, A3, A4), (A2, A3, A4)] {
    def tail = SimpleLens[(A1, A2, A3, A4), (A2, A3, A4)](t => (t._2, t._3, t._4), (t, a) => t.copy(_2 = a._1, _3 = a._2, _4 = a._3))
  }

  implicit def tuple5Tail[A1, A2, A3, A4, A5] = new Tail[(A1, A2, A3, A4, A5), (A2, A3, A4, A5)] {
    def tail = SimpleLens[(A1, A2, A3, A4, A5), (A2, A3, A4, A5)](t => (t._2, t._3, t._4, t._5), (t, a) => t.copy(_2 = a._1, _3 = a._2, _4 = a._3, _5 = a._4))
  }

  implicit def tuple6Tail[A1, A2, A3, A4, A5, A6] = new Tail[(A1, A2, A3, A4, A5, A6), (A2, A3, A4, A5, A6)] {
    def tail = SimpleLens[(A1, A2, A3, A4, A5, A6), (A2, A3, A4, A5, A6)](t => (t._2, t._3, t._4, t._5, t._6), (t, a) => t.copy(_2 = a._1, _3 = a._2, _4 = a._3, _5 = a._4, _6 = a._5))
  }

}