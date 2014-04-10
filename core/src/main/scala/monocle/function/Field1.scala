package monocle.function

import monocle.SimpleLens


trait Field1[S, A] {

  /** Creates a Lens from S to it is first element */
  def _1: SimpleLens[S, A]

}

object Field1 extends Field1Instances

trait Field1Instances {

  def _1[S, A](implicit ev: Field1[S, A]): SimpleLens[S, A] = ev._1

  implicit def tuple2Field1[A1, A2] = new Field1[(A1, A2), A1] {
    def _1 = SimpleLens[(A1, A2), A1](_._1, (t, a) => t.copy(_1 = a))
  }

  implicit def tuple3Field1[A1, A2, A3] = new Field1[(A1, A2, A3), A1] {
    def _1 = SimpleLens[(A1, A2, A3), A1](_._1, (t, a) => t.copy(_1 = a))
  }

  implicit def tuple4Field1[A1, A2, A3, A4] = new Field1[(A1, A2, A3, A4), A1] {
    def _1 = SimpleLens[(A1, A2, A3, A4), A1](_._1, (t, a) => t.copy(_1 = a))
  }

  implicit def tuple5Field1[A1, A2, A3, A4, A5] = new Field1[(A1, A2, A3, A4, A5), A1] {
    def _1 = SimpleLens[(A1, A2, A3, A4, A5), A1](_._1, (t, a) => t.copy(_1 = a))
  }

  implicit def tuple6Field1[A1, A2, A3, A4, A5, A6] = new Field1[(A1, A2, A3, A4, A5, A6), A1] {
    def _1 = SimpleLens[(A1, A2, A3, A4, A5, A6), A1](_._1, (t, a) => t.copy(_1 = a))
  }

}
