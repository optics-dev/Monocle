package monocle.function

import monocle.SimpleLens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field2[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field2[S, A] {

  /** Creates a Lens from S to it is second element */
  def second: SimpleLens[S, A]

}

object Field2 extends Field2Functions

trait Field2Functions {

  def second[S, A](implicit ev: Field2[S, A]): SimpleLens[S, A] = ev.second

}
