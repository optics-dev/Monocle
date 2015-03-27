package monocle.function

import monocle.std.tuple2._
import monocle.{Optional, Prism}

import scala.annotation.implicitNotFound
import scalaz.Maybe

@implicitNotFound("Could not find an instance of Cons[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Cons[S, A] {
 
  def cons: Prism[S, (A, S)]

  def headOption: Optional[S, A] = cons composeLens first
  def tailOption: Optional[S, S] = cons composeLens second

}

object Cons extends ConsFunctions


trait ConsFunctions {
  final def cons[S, A](implicit ev: Cons[S, A]): Prism[S, (A, S)] = ev.cons

  final def headOption[S, A](implicit ev: Cons[S, A]): Optional[S, A] = ev.headOption
  final def tailOption[S, A](implicit ev: Cons[S, A]): Optional[S, S] = ev.tailOption

  @deprecated("use headOption", since = "1.1.0")
  final def headMaybe[S, A](implicit ev: Cons[S, A]): Optional[S, A] = ev.headOption
  @deprecated("use tailOption", since = "1.1.0")
  final def tailMaybe[S, A](implicit ev: Cons[S, A]): Optional[S, S] = ev.tailOption

  /** append an element to the head */
  final def _cons[S, A](head: A, tail: S)(implicit ev: Cons[S, A]): S =
    ev.cons.reverseGet((head, tail))

  /** deconstruct an S between its head and tail */
  final def _uncons[S, A](s: S)(implicit ev: Cons[S, A]): Option[(A, S)] =
    ev.cons.getOption(s)
}