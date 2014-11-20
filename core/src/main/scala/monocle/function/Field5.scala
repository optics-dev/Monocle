package monocle.function

import monocle.Lens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field5[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field5[S, A] {

  /** Creates a Lens from S to it is fifth element */
  def fifth: Lens[S, A]

}

object Field5 extends Field5Functions

trait Field5Functions {

  def fifth[S, A](implicit ev: Field5[S, A]): Lens[S, A] = ev.fifth

}
