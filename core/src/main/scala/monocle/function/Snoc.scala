package monocle.function

import monocle.{Optional, Prism}
import monocle.std.tuple2._

import scala.annotation.implicitNotFound
import scalaz.Maybe


@implicitNotFound("Could not find an instance of Snoc[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Snoc[S, A] {

  def snoc: Prism[S, (S, A)]

  def initOption: Optional[S, S] = snoc composeLens first
  def lastOption: Optional[S, A] = snoc composeLens second

}

object Snoc extends SnocFunctions

trait SnocFunctions {
  final def snoc[S, A](implicit ev: Snoc[S, A]): Prism[S, (S, A)] = ev.snoc

  final def initOption[S, A](implicit ev: Snoc[S, A]): Optional[S, S] = ev.initOption
  final def lastOption[S, A](implicit ev: Snoc[S, A]): Optional[S, A] = ev.lastOption

  @deprecated("use initOption", since = "1.1.0")
  final def initMaybe[S, A](implicit ev: Snoc[S, A]): Optional[S, S] = ev.initOption
  @deprecated("use lastOption", since = "1.1.0")
  final def lastMaybe[S, A](implicit ev: Snoc[S, A]): Optional[S, A] = ev.lastOption

  /** append an element to the end */
  final def _snoc[S, A](init: S, last: A)(implicit ev: Snoc[S, A]): S =
    ev.snoc.reverseGet((init, last))

  /** deconstruct an S between its init and last */
  final def _unsnoc[S, A](s: S)(implicit ev: Snoc[S, A]): Option[(S, A)] =
    ev.snoc.getOption(s)
}