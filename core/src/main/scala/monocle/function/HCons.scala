package monocle.function

import monocle.std.tuple2._
import monocle.{SimpleIso, SimpleLens}

import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of HCons[${S}, ${H}, ${T}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait HCons[S, H, T] {

  def hcons: SimpleIso[S, (H, T)]

  final def head: SimpleLens[S, H] = hcons composeLens first
  final def tail: SimpleLens[S, T] = hcons composeLens second
}


object HCons extends HConsFunctions


trait HConsFunctions {
  final def hcons[S, H, T](implicit ev: HCons[S, H, T]): SimpleIso[S, (H, T)] = ev.hcons

  final def head[S, H, T](implicit ev: HCons[S, H, T]): SimpleLens[S, H] = ev.head
  final def tail[S, H, T](implicit ev: HCons[S, H, T]): SimpleLens[S, T] = ev.tail

  /** append an element to the head */
  final def _hcons[S, H, T](head: H, tail: T)(implicit ev: HCons[S, H, T]): S =
    ev.hcons.reverseGet((head, tail))

  /** deconstruct an S between its head and tail */
  final def _huncons[S, H, T](s: S)(implicit ev: HCons[S, H, T]): (H, T) =
    ev.hcons.get(s)
}