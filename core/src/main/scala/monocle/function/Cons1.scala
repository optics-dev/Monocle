package monocle.function

import monocle.std.tuple2._
import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Cons1[${S}, ${H}, ${T}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Cons1[S, H, T] extends Serializable {

  /** 
   * cons1 defines an [[Iso]] between a S and its head and tail.
   * cons1 is like cons but for types that have *always* a head and tail, e.g. a non empty list
   */
  def cons1: Iso[S, (H, T)]

  def head: Lens[S, H] = cons1 composeLens first
  def tail: Lens[S, T] = cons1 composeLens second
}


object Cons1 extends HConsFunctions


trait HConsFunctions {
  final def cons1[S, H, T](implicit ev: Cons1[S, H, T]): Iso[S, (H, T)] = ev.cons1

  final def head[S, H, T](implicit ev: Cons1[S, H, T]): Lens[S, H] = ev.head
  final def tail[S, H, T](implicit ev: Cons1[S, H, T]): Lens[S, T] = ev.tail

  /** append an element to the head */
  final def _cons1[S, H, T](head: H, tail: T)(implicit ev: Cons1[S, H, T]): S =
    ev.cons1.reverseGet((head, tail))

  /** deconstruct an S between its head and tail */
  final def _uncons1[S, H, T](s: S)(implicit ev: Cons1[S, H, T]): (H, T) =
    ev.cons1.get(s)
}