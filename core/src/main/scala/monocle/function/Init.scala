package monocle.function

import monocle.SimpleLens

trait Init[S, A] {

  /**
   * Creates a Lens between S and its init A.
   * Init represents all the the elements of S except the last one.
   * Init is strictly stronger than initOption as the presence of a
   * init for S is mandatory
   */
  def init: SimpleLens[S, A]

}

object Init extends InitInstances

trait InitInstances {

  def init[S, A](implicit ev: Init[S, A]): SimpleLens[S, A] = ev.init

  implicit def tuple2Init[A1, A2] = new Init[(A1, A2), A1] {
    def init = SimpleLens[(A1, A2), A1](_._1, (t, a) => t.copy(_1 = a))
  }

  implicit def tuple3Init[A1, A2, A3] = new Init[(A1, A2, A3), (A1, A2)] {
    def init = SimpleLens[(A1, A2, A3), (A1, A2)](t => (t._1, t._2), (t, a) => t.copy(_1 = a._1, _2 = a._2))
  }

  implicit def tuple4Init[A1, A2, A3, A4] = new Init[(A1, A2, A3, A4), (A1, A2, A3)] {
    def init = SimpleLens[(A1, A2, A3, A4), (A1, A2, A3)](t => (t._1, t._2, t._3), (t, a) => t.copy(_1 = a._1, _2 = a._2, _3 = a._3))
  }

  implicit def tuple5Init[A1, A2, A3, A4, A5] = new Init[(A1, A2, A3, A4, A5), (A1, A2, A3, A4)] {
    def init = SimpleLens[(A1, A2, A3, A4, A5), (A1, A2, A3, A4)](t => (t._1, t._2, t._3, t._4), (t, a) => t.copy(_1 = a._1, _2 = a._2, _3 = a._3, _4 = a._4))
  }

  implicit def tuple6Init[A1, A2, A3, A4, A5, A6] = new Init[(A1, A2, A3, A4, A5, A6), (A1, A2, A3, A4, A5)] {
    def init = SimpleLens[(A1, A2, A3, A4, A5, A6), (A1, A2, A3, A4, A5)](t => (t._1, t._2, t._3, t._4, t._5), (t, a) => t.copy(_1 = a._1, _2 = a._2, _3 = a._3, _4 = a._4, _5 = a._5))
  }

}
