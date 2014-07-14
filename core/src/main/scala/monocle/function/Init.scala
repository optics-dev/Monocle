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

object Init extends InitFunctions

trait InitFunctions {

  def init[S, A](implicit ev: Init[S, A]): SimpleLens[S, A] = ev.init

}
