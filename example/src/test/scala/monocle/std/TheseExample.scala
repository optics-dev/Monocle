package monocle.std

import monocle.MonocleSuite

import cats.data.Ior.{Left => This, Right => That, Both}
import cats.data.Ior
import cats.syntax.either._

class TheseExample extends MonocleSuite {
  test("theseToDisjunction is a prism between These and a Disjunction") {
    theseToDisjunction.getOption(This(5)        : Ior[Int, String]) shouldEqual Some(5.asLeft[String])
    theseToDisjunction.getOption(That("Hello")  : Ior[Int, String]) shouldEqual Some("Hello".asRight[Int])
    theseToDisjunction.getOption(Both(5,"Hello"): Ior[Int, String]) shouldEqual None

    theseToDisjunction.reverseGet(Left(5)       : Either[Int, String]) shouldEqual (This(5)      : Ior[Int, String])
    theseToDisjunction.reverseGet(Right("Hello"): Either[Int, String]) shouldEqual (That("Hello"): Ior[Int, String])
  }
}
