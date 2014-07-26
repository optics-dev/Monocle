package monocle.function

import monocle.SimpleLens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field1[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field1[S, A] {

  @deprecated("Use first", since = "0.5")
  def _1: SimpleLens[S, A] = first

  /** Creates a Lens from S to it is first element */
  def first: SimpleLens[S, A]

}

object Field1 extends Field1Functions

trait Field1Functions {

  @deprecated("Use first", since = "0.5")
  def _1[S, A](implicit ev: Field1[S, A]): SimpleLens[S, A] = ev._1

  def first[S, A](implicit ev: Field1[S, A]): SimpleLens[S, A] = ev.first

}
