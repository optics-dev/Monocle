package monocle.function

import monocle.Lens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field4[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field4[S, A] extends Serializable {

  /** Creates a Lens from S to it is fourth element */
  def fourth: Lens[S, A]

}

object Field4 extends Field4Functions

trait Field4Functions {

  def fourth[S, A](implicit ev: Field4[S, A]): Lens[S, A] = ev.fourth

}
