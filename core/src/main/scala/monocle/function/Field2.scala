package monocle.function

import monocle._


trait Field2[S, A] {

  /** Creates a Lens from S to it is second element */
  def _2: SimpleLens[S, A]

}

object Field2 extends Field2Instances

trait Field2Instances {

  def _2[S, A](implicit ev: Field2[S, A]): SimpleLens[S, A] = ev._2

  implicit def tuple2Field2[A1, A2] = new Field2[(A1, A2), A2] {
    def _2 = SimpleLens[(A1, A2), A2](_._2, (t, a) => t.copy(_2 = a))
  }

  implicit def tuple3Field2[A1, A2, A3] = new Field2[(A1, A2, A3), A2] {
    def _2 = SimpleLens[(A1, A2, A3), A2](_._2, (t, a) => t.copy(_2 = a))
  }

  implicit def tuple4Field2[A1, A2, A3, A4] = new Field2[(A1, A2, A3, A4), A2] {
    def _2 = SimpleLens[(A1, A2, A3, A4), A2](_._2, (t, a) => t.copy(_2 = a))
  }

  implicit def tuple5Field2[A1, A2, A3, A4, A5] = new Field2[(A1, A2, A3, A4, A5), A2] {
    def _2 = SimpleLens[(A1, A2, A3, A4, A5), A2](_._2, (t, a) => t.copy(_2 = a))
  }

  implicit def tuple6Field2[A1, A2, A3, A4, A5, A6] = new Field2[(A1, A2, A3, A4, A5, A6), A2] {
    def _2 = SimpleLens[(A1, A2, A3, A4, A5, A6), A2](_._2, (t, a) => t.copy(_2 = a))
  }

}
