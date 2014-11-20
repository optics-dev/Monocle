package monocle.function

import monocle.Iso
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Reverse[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Reverse[S, A] {

  /** Creates an Iso from S to a reversed S */
  def reverse: Iso[S, A]

}

object Reverse extends ReverseFunctions

trait ReverseFunctions {

  def reverseFromReverseFunction[S](_reverse: S => S): Reverse[S, S] = new Reverse[S, S] {
    def reverse = Iso(_reverse)(_reverse)
  }

  def reverse[S, A](implicit ev: Reverse[S, A]): Iso[S, A] = ev.reverse

  def _reverse[S](s: S)(implicit ev: Reverse[S, S]): S = ev.reverse.get(s)
}