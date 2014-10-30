package monocle.function

import monocle.std.tuple2._
import monocle.{SimpleIso, SimpleLens}

import scala.annotation.implicitNotFound


@implicitNotFound("Could not find an instance of HSnoc[${S}, ${I}, ${L}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Snoc1[S, I, L] {

  /**
   * snoc1 defines an [[SimpleIso]] between a S and its init and last element.
   * snoc1 is like snoc but for types that have *always* an init and a last element, e.g. a non empty list
   */
  def snoc1: SimpleIso[S, (I, L)]

  def init: SimpleLens[S, I] = snoc1 composeLens first
  def last: SimpleLens[S, L] = snoc1 composeLens second

}

object Snoc1 extends Snoc1Functions

trait Snoc1Functions {
  final def snoc1[S, I, L](implicit ev: Snoc1[S, I, L]): SimpleIso[S, (I, L)] = ev.snoc1

  final def init[S, I, L](implicit ev: Snoc1[S, I, L]): SimpleLens[S, I] = ev.init
  final def last[S, I, L](implicit ev: Snoc1[S, I, L]): SimpleLens[S, L] = ev.last

  /** append an element to the end */
  final def _snoc1[S, I, L](init: I, last: L)(implicit ev: Snoc1[S, I, L]): S =
    ev.snoc1.reverseGet((init, last))

  /** deconstruct an S between its init and last */
  final def _unsnoc1[S, I, L](s: S)(implicit ev: Snoc1[S, I, L]): (I, L) =
    ev.snoc1.get(s)
}