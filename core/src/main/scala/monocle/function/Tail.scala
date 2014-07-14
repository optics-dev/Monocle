package monocle.function

import monocle.SimpleLens

trait Tail[S, A] {

  /**
   * Creates an Lens between S and its tail A.
   * tail is strictly stronger than tailOption as the presence of a
   * tail for S is mandatory
   */
  def tail: SimpleLens[S, A]

}

object Tail extends TailFunctions

trait TailFunctions {

  def tail[S, A](implicit ev: Tail[S, A]): SimpleLens[S, A] = ev.tail

}