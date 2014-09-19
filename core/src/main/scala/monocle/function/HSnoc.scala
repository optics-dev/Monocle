package monocle.function

import monocle.std.tuple2._
import monocle.{SimpleIso, SimpleLens}

import scala.annotation.implicitNotFound


@implicitNotFound("Could not find an instance of HSnoc[${S}, ${I}, ${L}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait HSnoc[S, I, L] {

  def hsnoc: SimpleIso[S, (I, L)]

  final def init: SimpleLens[S, I] = hsnoc composeLens first
  final def last: SimpleLens[S, L] = hsnoc composeLens second

}

object HSnoc extends HSnocFunctions

trait HSnocFunctions {
  final def hsnoc[S, I, L](implicit ev: HSnoc[S, I, L]): SimpleIso[S, (I, L)] = ev.hsnoc

  final def init[S, I, L](implicit ev: HSnoc[S, I, L]): SimpleLens[S, I] = ev.init
  final def last[S, I, L](implicit ev: HSnoc[S, I, L]): SimpleLens[S, L] = ev.last

  /** append an element to the end */
  final def _hsnoc[S, I, L](init: I, last: L)(implicit ev: HSnoc[S, I, L]): S =
    ev.hsnoc.reverseGet((init, last))

  /** deconstruct an S between its init and last */
  final def _hunsnoc[S, I, L](s: S)(implicit ev: HSnoc[S, I, L]): (I, L) =
    ev.hsnoc.get(s)
}