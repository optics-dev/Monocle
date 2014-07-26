package monocle.function

import monocle.SimpleLens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field6[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field6[S, A] {

  @deprecated("Use sixth", since = "0.5")
  def _6: SimpleLens[S, A] = sixth

  /** Creates a Lens from S to it is sixth element */
  def sixth: SimpleLens[S, A]

}

object Field6 extends Field6Functions

trait Field6Functions {

  @deprecated("Use sixth", since = "0.5")
  def _6[S, A](implicit ev: Field6[S, A]): SimpleLens[S, A] = ev._6

  def sixth[S, A](implicit ev: Field6[S, A]): SimpleLens[S, A] = ev.sixth

}