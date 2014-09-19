package monocle.function

import monocle.std.tuple2._
import monocle.{SimpleOptional, SimplePrism}

import scala.annotation.implicitNotFound
import scalaz.Maybe

@implicitNotFound("Could not find an instance of Cons[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Cons[S, A] {
 
  def cons: SimplePrism[S, (A, S)]

  final def headMaybe: SimpleOptional[S, A] = cons composeLens first
  final def tailMaybe: SimpleOptional[S, S] = cons composeLens second

}

object Cons extends ConsFunctions


trait ConsFunctions {
  final def cons[S, A](implicit ev: Cons[S, A]): SimplePrism[S, (A, S)] = ev.cons

  final def headMaybe[S, A](implicit ev: Cons[S, A]): SimpleOptional[S, A] = ev.headMaybe
  final def tailMaybe[S, A](implicit ev: Cons[S, A]): SimpleOptional[S, S] = ev.tailMaybe

  /** append an element to the head */
  final def _cons[S, A](head: A, tail: S)(implicit ev: Cons[S, A]): S =
    ev.cons.reverseGet((head, tail))

  /** deconstruct an S between its head and tail */
  final def _uncons[S, A](s: S)(implicit ev: Cons[S, A]): Maybe[(A, S)] =
    ev.cons.getMaybe(s)
}