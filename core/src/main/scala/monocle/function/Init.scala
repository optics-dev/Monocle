package monocle.function

import monocle.Lens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Init[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Init[S, A] {

  /**
   * Creates a Lens between S and its init A.
   * Init represents all the the elements of S except the last one.
   * Init is strictly stronger than initOption as the presence of a
   * init for S is mandatory
   */
  def init: Lens[S, A]

}

object Init extends InitFunctions

trait InitFunctions {

  def init[S, A](implicit ev: Init[S, A]): Lens[S, A] = ev.init

}
