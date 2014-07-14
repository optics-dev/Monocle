package monocle.function

import monocle._


trait Field2[S, A] {

  @deprecated("Use second", since = "0.5.0")
  def _2: SimpleLens[S, A] = second

  /** Creates a Lens from S to it is second element */
  def second: SimpleLens[S, A]

}

object Field2 extends Field2Functions

trait Field2Functions {

  @deprecated("Use second", since = "0.5.0")
  def _2[S, A](implicit ev: Field2[S, A]): SimpleLens[S, A] = ev._2

  def second[S, A](implicit ev: Field2[S, A]): SimpleLens[S, A] = ev.second

}
