package monocle.function

import monocle._

trait Field4[S, A] {

  @deprecated("Use fourth", since = "0.5.0")
  def _4: SimpleLens[S, A] = fourth

  /** Creates a Lens from S to it is fourth element */
  def fourth: SimpleLens[S, A]

}

object Field4 extends Field4Instances

trait Field4Instances {

  @deprecated("Use fourth", since = "0.5.0")
  def _4[S, A](implicit ev: Field4[S, A]): SimpleLens[S, A] = ev._4

  def fourth[S, A](implicit ev: Field4[S, A]): SimpleLens[S, A] = ev.fourth

  implicit def tuple4Field4[A1, A2, A3, A4] = new Field4[(A1, A2, A3, A4), A4] {
    def fourth = SimpleLens[(A1, A2, A3, A4), A4](_._4, (t, a) => t.copy(_4 = a))
  }

  implicit def tuple5Field4[A1, A2, A3, A4, A5] = new Field4[(A1, A2, A3, A4, A5), A4] {
    def fourth = SimpleLens[(A1, A2, A3, A4, A5), A4](_._4, (t, a) => t.copy(_4 = a))
  }

  implicit def tuple6Field4[A1, A2, A3, A4, A5, A6] = new Field4[(A1, A2, A3, A4, A5, A6), A4] {
    def fourth = SimpleLens[(A1, A2, A3, A4, A5, A6), A4](_._4, (t, a) => t.copy(_4 = a))
  }

}
