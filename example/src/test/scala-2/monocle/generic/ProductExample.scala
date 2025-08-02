package monocle.generic

import monocle.MonocleSuite

import scala.annotation.nowarn

case class Example(i: Int, s: String, b: Boolean)

@nowarn
class ProductExample extends MonocleSuite with GenericInstances {
  test("productToTuple creates an Iso between a Product and a Tuple") {
    assertEquals((Example(1, "Hello", true) applyIso productToTuple).get, (1, "Hello", true))
    assertEquals(productToTuple[Example].reverseGet((1, "Hello", true)), Example(1, "Hello", true))
  }
}
