package monocle.generic

import org.specs2.execute.AnyValueAsResult
import org.specs2.scalaz.Spec
import shapeless.test.illTyped
import shapeless.{:+:, CNil, Coproduct}

class CoproductExample extends Spec {

  type ISB = Int :+: String :+: Boolean :+: CNil


  "coProductPrism creates a Prism between a Coproduct and one of its choice" in {
    val b = Coproduct[ISB](true)
    val i = Coproduct[ISB](3)

    coProductPrism[ISB, Int].getOption(i) ==== Some(3)
    coProductPrism[ISB, Int].getOption(b) ==== None

    coProductPrism[ISB, Boolean].getOption(i) ==== None
    coProductPrism[ISB, Boolean].getOption(b) ==== Some(true)

    coProductPrism[ISB, Boolean].reverseGet(true) ==== b
  }

  "coProductPrism can only create Prism to one of the type of the Coproduct" in {
    new AnyValueAsResult[Unit].asResult(
      illTyped("""coProductPrism[ISB, Float]""")
    )

  }

}
