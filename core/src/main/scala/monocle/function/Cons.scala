package monocle.function

import monocle.std.tuple2._
import monocle.{SimpleOptional, SimplePrism}

import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Cons[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Cons[S, A] {
 
  def _cons: SimplePrism[S, (A, S)]

  final def headOption: SimpleOptional[S, A] = _cons composeLens first
  final def tailOption: SimpleOptional[S, S] = _cons composeLens second

}

object Cons extends ConsFunctions with ConsFunctionsAfterDeprecation


trait ConsFunctions {
  final def cons[S, A](implicit ev: Cons[S, A]): SimplePrism[S, (A, S)] = ev._cons

  /** append an element to the head */
  final def _cons[S, A](head: A, tail: S)(implicit ev: Cons[S, A]): S =
    ev._cons.reverseGet((head, tail))

  /** deconstruct an S between its head and tail */
  final def _uncons[S, A](s: S)(implicit ev: Cons[S, A]): Option[(A, S)] =
    ev._cons.getOption(s)
}

// To merge into ConsFunctions when HeadOption and LastOption are deprecated
sealed trait ConsFunctionsAfterDeprecation {
  final def headOption[S, A](implicit ev: Cons[S, A]): SimpleOptional[S, A] = ev.headOption
  final def tailOption[S, A](implicit ev: Cons[S, A]): SimpleOptional[S, S] = ev.tailOption
}