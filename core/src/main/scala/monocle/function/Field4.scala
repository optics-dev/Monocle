package monocle.function

import monocle.SimpleLens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field4[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field4[S, A] {

  @deprecated("Use fourth", since = "0.5")
  def _4: SimpleLens[S, A] = fourth

  /** Creates a Lens from S to it is fourth element */
  def fourth: SimpleLens[S, A]

}

object Field4 extends Field4Functions

trait Field4Functions {

  @deprecated("Use fourth", since = "0.5")
  def _4[S, A](implicit ev: Field4[S, A]): SimpleLens[S, A] = ev._4

  def fourth[S, A](implicit ev: Field4[S, A]): SimpleLens[S, A] = ev.fourth

}
