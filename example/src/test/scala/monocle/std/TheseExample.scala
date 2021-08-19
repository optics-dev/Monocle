package monocle.std

import monocle.MonocleSuite

import cats.data.Ior.{Both, Left => This, Right => That}
import cats.data.Ior
import cats.syntax.either._

class TheseExample extends MonocleSuite {
  test("theseToDisjunction is a prism between These and a Disjunction") {
    assertEquals(theseToDisjunction.getOption(This(5): Ior[Int, String]), Some(5.asLeft[String]))
    assertEquals(theseToDisjunction.getOption(That("Hello"): Ior[Int, String]), Some("Hello".asRight[Int]))
    assertEquals(theseToDisjunction.getOption(Both(5, "Hello"): Ior[Int, String]), None)

    assertEquals(theseToDisjunction.reverseGet(Left(5): Either[Int, String]), (This(5): Ior[Int, String]))
    assertEquals(theseToDisjunction.reverseGet(Right("Hello"): Either[Int, String]), (That("Hello"): Ior[Int, String]))
  }
}
