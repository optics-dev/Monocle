package monocle.function

import monocle.{SimpleOptional, SimplePrism}
import monocle.std.tuple2._

import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Cons[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Cons[S, A] {
 
  def _cons: SimplePrism[S, (A, S)]

  final def headOption: SimpleOptional[S, A] = _cons composeOptional first
  final def tailOption: SimpleOptional[S, S] = _cons composeOptional second

}

object Cons extends ConsFunctions with ConsFunctionsAfterDeprecation


trait ConsFunctions {

  final def _cons[S, A](implicit ev: Cons[S, A]): SimplePrism[S, (A, S)] = ev._cons

  /** append an element to the head */
  final def cons[S, A](head: A, tail: S)(implicit ev: Cons[S, A]): S =
    ev._cons.reverseGet((head, tail))

  /** deconstruct an S between its head and tail */
  final def uncons[S, A](s: S)(implicit ev: Cons[S, A]): Option[(A, S)] =
    ev._cons.getOption(s)

  final def fromReverseSnoc[S, A](implicit evSnoc: Snoc[S, A], evReverse: Reverse[S, S]): Cons[S, A] = new Cons[S, A]{
    def _cons = evReverse.reverse composePrism evSnoc._snoc composePrism reverse
  }
}

// To merge into ConsFunctions when HeadOption and LastOption are deprecated
sealed trait ConsFunctionsAfterDeprecation {
  final def headOption[S, A](implicit ev: Cons[S, A]): SimpleOptional[S, A] = ev.headOption
  final def tailOption[S, A](implicit ev: Cons[S, A]): SimpleOptional[S, S] = ev.tailOption
}