package monocle.generic

import monocle.MonocleSuite

case class Example(i : Int, s: String, b: Boolean)

class ProductExample extends MonocleSuite {

  test("productToTuple creates an Iso between a Product and a Tuple") {
    (Example(1, "Hello", true) applyIso productToTuple).get shouldEqual ((1, "Hello", true))
    productToTuple[Example].reverseGet((1, "Hello", true)) shouldEqual Example(1, "Hello", true)
  }

}
