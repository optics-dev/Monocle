package monocle.function

import monocle.function.fields._
import monocle.std.tuple2._
import monocle.{Iso, Optional, Prism}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines a [[Prism]] between an `S` and its init `S` and last `S`
 * @tparam S source of [[Prism]] and init of [[Prism]] target
 * @tparam A last of [[Prism]] target, `A` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Snoc[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Snoc[S, A] extends Serializable {
  def snoc: Prism[S, (S, A)]

  def initOption: Optional[S, S] = snoc composeLens first
  def lastOption: Optional[S, A] = snoc composeLens second
}

object Snoc extends SnocFunctions {
  /** lift an instance of [[Snoc]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Snoc[A, B]): Snoc[S, B] = new Snoc[S, B] {
    override def snoc: Prism[S, (S, B)] =
      iso composePrism ev.snoc composeIso iso.reverse.first
  }
}

trait SnocFunctions {
  final def snoc[S, A](implicit ev: Snoc[S, A]): Prism[S, (S, A)] = ev.snoc

  final def initOption[S, A](implicit ev: Snoc[S, A]): Optional[S, S] = ev.initOption
  final def lastOption[S, A](implicit ev: Snoc[S, A]): Optional[S, A] = ev.lastOption

  /** append an element to the end */
  final def _snoc[S, A](init: S, last: A)(implicit ev: Snoc[S, A]): S =
    ev.snoc.reverseGet((init, last))

  /** deconstruct an S between its init and last */
  final def _unsnoc[S, A](s: S)(implicit ev: Snoc[S, A]): Option[(S, A)] =
    ev.snoc.getOption(s)
}