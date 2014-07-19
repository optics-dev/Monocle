package monocle.function

import monocle.SimpleLens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field5[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field5[S, A] {

  @deprecated("Use fifth", since = "0.5.0")
  def _5: SimpleLens[S, A] = fifth

  /** Creates a Lens from S to it is fifth element */
  def fifth: SimpleLens[S, A]

}

object Field5 extends Field5Functions

trait Field5Functions {

  @deprecated("Use fifth", since = "0.5.0")
  def _5[S, A](implicit ev: Field5[S, A]): SimpleLens[S, A] = ev._5

  def fifth[S, A](implicit ev: Field5[S, A]): SimpleLens[S, A] = ev.fifth

}
