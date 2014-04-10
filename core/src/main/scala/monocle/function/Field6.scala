package monocle.function

import monocle._

trait Field6[S, A] {

  /** Creates a Lens from S to it is sixth element */
  def _6: SimpleLens[S, A]

}

object Field6 extends Field6Instances

trait Field6Instances {

  def _6[S, A](implicit ev: Field6[S, A]): SimpleLens[S, A] = ev._6

  implicit def tuple6Field6[A1, A2, A3, A4, A5, A6] = new Field6[(A1, A2, A3, A4, A5, A6), A6] {
    def _6 = SimpleLens[(A1, A2, A3, A4, A5, A6), A6](_._6, (t, a) => t.copy(_6 = a))
  }

}