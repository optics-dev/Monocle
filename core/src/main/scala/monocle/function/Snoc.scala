package monocle.function

import monocle.{SimpleOptional, SimplePrism}
import monocle.std.tuple2._

import scala.annotation.implicitNotFound


@implicitNotFound("Could not find an instance of Snoc[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Snoc[S, A] {

  def _snoc: SimplePrism[S, (S, A)]

  final def initOption: SimpleOptional[S, S] = _snoc composeOptional first
  final def lastOption: SimpleOptional[S, A] = _snoc composeOptional second

}

object Snoc extends SnocFunctions with SnocFunctionsAfterDeprecation

trait SnocFunctions {
  def _snoc[S, A](implicit ev: Snoc[S, A]): SimplePrism[S, (S, A)] = ev._snoc

  /** append an element to the end */
  def snoc[S, A](init: S, last: A)(implicit ev: Snoc[S, A]): S =
    ev._snoc.reverseGet((init, last))

  /** deconstruct an S between its init and last */
  def unsnoc[S, A](s: S)(implicit ev: Snoc[S, A]): Option[(S, A)] =
    ev._snoc.getOption(s)

  def fromReverseCons[S, A](implicit evCons: Cons[S, A], evReverse: Reverse[S, S]): Snoc[S, A] = new Snoc[S, A]{
    def _snoc = evReverse.reverse composePrism evCons._cons composePrism reverse
  }
}

// To merge into ConsFunctions when HeadOption and LastOption are deprecated
sealed trait SnocFunctionsAfterDeprecation {
  def initOption[S, A](implicit ev: Snoc[S, A]): SimpleOptional[S, S] = ev.initOption
  def lastOption[S, A](implicit ev: Snoc[S, A]): SimpleOptional[S, A] = ev.lastOption
}