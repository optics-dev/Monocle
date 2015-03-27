package monocle.std

import org.specs2.scalaz.Spec

import scalaz.\&/._
import scalaz._
import scalaz.syntax.either._

class TheseExample extends Spec {
  "theseDisjunction is a prism between These and a Disjunction" in {
    theseDisjunction.getOption(This(5)        : Int \&/ String) ==== Some(5.left[String])
    theseDisjunction.getOption(That("Hello")  : Int \&/ String) ==== Some("Hello".right[Int])
    theseDisjunction.getOption(Both(5,"Hello"): Int \&/ String) ==== None

    theseDisjunction.reverseGet(-\/(5)      : Int \/ String) ==== (This(5)      : Int \&/ String)
    theseDisjunction.reverseGet(\/-("Hello"): Int \/ String) ==== (That("Hello"): Int \&/ String)
  }
}
