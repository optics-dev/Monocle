package monocle.function

import monocle.SimpleLens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field3[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field3[S, A] {

  /** Creates a Lens from S to it is third element */
  def third: SimpleLens[S, A]

}

object Field3 extends Field3Functions

trait Field3Functions {

  def third[S, A](implicit ev: Field3[S, A]): SimpleLens[S, A] = ev.third

}