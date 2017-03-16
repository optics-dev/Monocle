package monocle.std

import monocle.MonocleSuite

import cats.data.{Ior => \&/}
import cats.data.Ior.{Left => This, Right => That, Both}
import cats.syntax.either._
import scala.{Either => \/, Left => -\/, Right =>  \/-}

class TheseExample extends MonocleSuite {
  test("theseToDisjunction is a prism between These and a Disjunction") {
    theseToDisjunction.getOption(This(5)        : Int \&/ String) shouldEqual Some(5.asLeft[String])
    theseToDisjunction.getOption(That("Hello")  : Int \&/ String) shouldEqual Some("Hello".asRight[Int])
    theseToDisjunction.getOption(Both(5,"Hello"): Int \&/ String) shouldEqual None

    theseToDisjunction.reverseGet(-\/(5)      : Int \/ String) shouldEqual (This(5)      : Int \&/ String)
    theseToDisjunction.reverseGet(\/-("Hello"): Int \/ String) shouldEqual (That("Hello"): Int \&/ String)
  }
}
