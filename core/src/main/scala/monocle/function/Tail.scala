package monocle.function

import monocle.Lens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Tail[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Tail[S, A] {

  /**
   * Creates an Lens between S and its tail A.
   * tail is strictly stronger than tailOption as the presence of a
   * tail for S is mandatory
   */
  def tail: Lens[S, A]

}

object Tail extends TailFunctions

trait TailFunctions {

  def tail[S, A](implicit ev: Tail[S, A]): Lens[S, A] = ev.tail

}