package monocle.function

import monocle.{SimpleOptional, SimplePrism}
import monocle.std.tuple2._

import scala.annotation.implicitNotFound
import scalaz.Maybe


@implicitNotFound("Could not find an instance of Snoc[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Snoc[S, A] {

  def snoc: SimplePrism[S, (S, A)]

  final def initOption: SimpleOptional[S, S] = snoc composeLens first
  final def lastOption: SimpleOptional[S, A] = snoc composeLens second

}

object Snoc extends SnocFunctions with SnocFunctionsAfterDeprecation

trait SnocFunctions {
  final def snoc[S, A](implicit ev: Snoc[S, A]): SimplePrism[S, (S, A)] = ev.snoc

  /** append an element to the end */
  final def _snoc[S, A](init: S, last: A)(implicit ev: Snoc[S, A]): S =
    ev.snoc.reverseGet((init, last))

  /** deconstruct an S between its init and last */
  final def _unsnoc[S, A](s: S)(implicit ev: Snoc[S, A]): Maybe[(S, A)] =
    ev.snoc.getMaybe(s)
}

// To merge into ConsFunctions when HeadOption and LastOption are deprecated
sealed trait SnocFunctionsAfterDeprecation {
  final def initOption[S, A](implicit ev: Snoc[S, A]): SimpleOptional[S, S] = ev.initOption
  final def lastOption[S, A](implicit ev: Snoc[S, A]): SimpleOptional[S, A] = ev.lastOption
}