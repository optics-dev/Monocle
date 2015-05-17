package monocle.generic

import monocle.MonocleSuite
import shapeless.test.illTyped
import shapeless.{:+:, CNil, Coproduct}

class CoproductExample extends MonocleSuite {

  type ISB = Int :+: String :+: Boolean :+: CNil


  test("coProductPrism creates a Prism between a Coproduct and one of its choice") {
    val b = Coproduct[ISB](true)
    val i = Coproduct[ISB](3)

    coProductPrism[ISB, Int].getOption(i) shouldEqual Some(3)
    coProductPrism[ISB, Int].getOption(b) shouldEqual None

    coProductPrism[ISB, Boolean].getOption(i) shouldEqual None
    coProductPrism[ISB, Boolean].getOption(b) shouldEqual Some(true)

    coProductPrism[ISB, Boolean].reverseGet(true) shouldEqual b
  }

  test("coProductPrism can only create Prism to one of the type of the Coproduct") {
      illTyped("""coProductPrism[ISB, Float]""")
  }

}
