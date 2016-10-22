package monocle.function

import monocle.function.fields._
import monocle.std.tuple2._
import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines an [[Iso]] between an `S` and its init `H` and last `T`
 * [[Snoc1]] is like [[Snoc]] but for types that have *always* an init and a last element, e.g. a non empty list
 * @tparam S source of [[Iso]]
 * @tparam I init of [[Iso]] target, `I` is supposed to be unique for a given `S`
 * @tparam L last of [[Iso]] target, `L` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Snoc1[${S}, ${I}, ${L}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Snoc1[S, I, L] extends Serializable {
  def snoc1: Iso[S, (I, L)]

  def init: Lens[S, I] = snoc1 composeLens first
  def last: Lens[S, L] = snoc1 composeLens second
}

object Snoc1 extends Snoc1Functions {
  /** lift an instance of [[Snoc1]] using an [[Iso]] */
  def fromIso[S, A, I, L](iso: Iso[S, A])(implicit ev: Snoc1[A, I, L]): Snoc1[S, I, L] = new Snoc1[S, I, L] {
    override def snoc1: Iso[S, (I, L)] =
      iso composeIso ev.snoc1
  }
}

trait Snoc1Functions {
  final def snoc1[S, I, L](implicit ev: Snoc1[S, I, L]): Iso[S, (I, L)] = ev.snoc1

  final def init[S, I, L](implicit ev: Snoc1[S, I, L]): Lens[S, I] = ev.init
  final def last[S, I, L](implicit ev: Snoc1[S, I, L]): Lens[S, L] = ev.last

  /** append an element to the end */
  final def _snoc1[S, I, L](init: I, last: L)(implicit ev: Snoc1[S, I, L]): S =
    ev.snoc1.reverseGet((init, last))

  /** deconstruct an S between its init and last */
  final def _unsnoc1[S, I, L](s: S)(implicit ev: Snoc1[S, I, L]): (I, L) =
    ev.snoc1.get(s)
}