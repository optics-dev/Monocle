package monocle.std

import monocle.MonocleSuite

import scalaz.\&/._
import scalaz._
import scalaz.syntax.either._

class TheseExample extends MonocleSuite {
  test("theseToDisjunction is a prism between These and a Disjunction") {
    theseToDisjunction.getOption(This(5)        : Int \&/ String) shouldEqual Some(5.left[String])
    theseToDisjunction.getOption(That("Hello")  : Int \&/ String) shouldEqual Some("Hello".right[Int])
    theseToDisjunction.getOption(Both(5,"Hello"): Int \&/ String) shouldEqual None

    theseToDisjunction.reverseGet(-\/(5)      : Int \/ String) shouldEqual (This(5)      : Int \&/ String)
    theseToDisjunction.reverseGet(\/-("Hello"): Int \/ String) shouldEqual (That("Hello"): Int \&/ String)
  }
}
