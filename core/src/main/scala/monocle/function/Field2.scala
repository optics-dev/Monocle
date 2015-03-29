package monocle.function

import monocle.Lens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field2[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field2[S, A] extends Serializable {

  /** Creates a Lens from S to it is second element */
  def second: Lens[S, A]

}

object Field2 extends Field2Functions

trait Field2Functions {

  def second[S, A](implicit ev: Field2[S, A]): Lens[S, A] = ev.second

}
