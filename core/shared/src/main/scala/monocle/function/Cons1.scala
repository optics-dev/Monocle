package monocle.function

import monocle.function.fields._
import monocle.std.tuple2._
import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines an [[Iso]] between an `S` and its head `H` and tail `T`
 * [[Cons1]] is like [[Cons]] but for types that have *always* an head and tail, e.g. a non empty list
 * @tparam S source of [[Iso]]
 * @tparam H head of [[Iso]] target, `A` is supposed to be unique for a given `S`
 * @tparam T tail of [[Iso]] target, `T` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Cons1[${S}, ${H}, ${T}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Cons1[S, H, T] extends Serializable {
  def cons1: Iso[S, (H, T)]

  def head: Lens[S, H] = cons1 composeLens first
  def tail: Lens[S, T] = cons1 composeLens second
}


object Cons1 extends Cons1Functions {
  /** lift an instance of [[Cons1]] using an [[Iso]] */
  def fromIso[S, A, H, T](iso: Iso[S, A])(implicit ev: Cons1[A, H, T]): Cons1[S, H, T] = new Cons1[S, H, T] {
    override def cons1: Iso[S, (H, T)] =
      iso composeIso ev.cons1
  }
}


trait Cons1Functions {
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