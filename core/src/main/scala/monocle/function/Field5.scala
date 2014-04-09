package monocle.function

import monocle._

trait Field5[S, A] {

  def _5: SimpleLens[S, A]

}

object Field5 extends Field5Instances

trait Field5Instances {

  def _5[S, A](implicit ev: Field5[S, A]): SimpleLens[S, A] = ev._5

  implicit def tuple5Field5[A1, A2, A3, A4, A5] = new Field5[(A1, A2, A3, A4, A5), A5] {
    def _5 = SimpleLens[(A1, A2, A3, A4, A5), A5](_._5, (t, a) => t.copy(_5 = a))
  }

  implicit def tuple6Field5[A1, A2, A3, A4, A5, A6] = new Field5[(A1, A2, A3, A4, A5, A6), A5] {
    def _5 = SimpleLens[(A1, A2, A3, A4, A5, A6), A5](_._5, (t, a) => t.copy(_5 = a))
  }

}
