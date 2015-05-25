package monocle.std

import monocle.MonocleSuite

import scalaz.\&/._
import scalaz._
import scalaz.syntax.either._

class TheseExample extends MonocleSuite {
  test("theseDisjunction is a prism between These and a Disjunction") {
    theseDisjunction.getOption(This(5)        : Int \&/ String) shouldEqual Some(5.left[String])
    theseDisjunction.getOption(That("Hello")  : Int \&/ String) shouldEqual Some("Hello".right[Int])
    theseDisjunction.getOption(Both(5,"Hello"): Int \&/ String) shouldEqual None

    theseDisjunction.reverseGet(-\/(5)      : Int \/ String) shouldEqual (This(5)      : Int \&/ String)
    theseDisjunction.reverseGet(\/-("Hello"): Int \/ String) shouldEqual (That("Hello"): Int \&/ String)
  }
}
