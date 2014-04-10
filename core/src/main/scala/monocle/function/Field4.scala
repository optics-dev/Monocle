package monocle.function

import monocle._

trait Field4[S, A] {

  /** Creates a Lens from S to it is fourth element */
  def _4: SimpleLens[S, A]

}

object Field4 extends Field4Instances

trait Field4Instances {

  def _4[S, A](implicit ev: Field4[S, A]): SimpleLens[S, A] = ev._4

  implicit def tuple4Field4[A1, A2, A3, A4] = new Field4[(A1, A2, A3, A4), A4] {
    def _4 = SimpleLens[(A1, A2, A3, A4), A4](_._4, (t, a) => t.copy(_4 = a))
  }

  implicit def tuple5Field4[A1, A2, A3, A4, A5] = new Field4[(A1, A2, A3, A4, A5), A4] {
    def _4 = SimpleLens[(A1, A2, A3, A4, A5), A4](_._4, (t, a) => t.copy(_4 = a))
  }

  implicit def tuple6Field4[A1, A2, A3, A4, A5, A6] = new Field4[(A1, A2, A3, A4, A5, A6), A4] {
    def _4 = SimpleLens[(A1, A2, A3, A4, A5, A6), A4](_._4, (t, a) => t.copy(_4 = a))
  }

}
