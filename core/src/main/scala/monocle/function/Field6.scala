package monocle.function

import monocle.Lens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field6[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field6[S, A] extends Serializable {

  /** Creates a Lens from S to it is sixth element */
  def sixth: Lens[S, A]

}

object Field6 extends Field6Functions

trait Field6Functions {

  def sixth[S, A](implicit ev: Field6[S, A]): Lens[S, A] = ev.sixth

}