package monocle

import org.specs2.scalaz.Spec
import shapeless.{Coproduct, CNil, :+:}
import monocle.thirdparty.coproduct._
import monocle.function.SafeCast._
import shapeless.test.illTyped


class CoproductExample extends Spec {

  type ISB = Int :+: String :+: Boolean :+: CNil


  "coProductPrism creates a Prism between a Coproduct and one of its choice" in {

    val b = Coproduct[ISB](true)
    val i = Coproduct[ISB](3)

    safeCast[ISB, Int].getOption(i) shouldEqual Some(3)
    safeCast[ISB, Int].getOption(b) shouldEqual None

    safeCast[ISB, Boolean].getOption(i) shouldEqual None
    safeCast[ISB, Boolean].getOption(b) shouldEqual Some(true)

    safeCast[ISB, Boolean].reverseGet(true) shouldEqual b

  }

  "coProductPrism can only create Prism to one of the type of the Coproduct" in {

    illTyped("""
      safeCast[ISB, Float]
    """)

  }

}
