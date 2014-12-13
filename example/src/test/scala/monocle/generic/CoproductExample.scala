package monocle.generic

import org.specs2.execute.AnyValueAsResult
import org.specs2.scalaz.Spec
import shapeless.test.illTyped
import shapeless.{:+:, CNil, Coproduct}

import scalaz.Maybe


class CoproductExample extends Spec {

  type ISB = Int :+: String :+: Boolean :+: CNil


  "coProductPrism creates a Prism between a Coproduct and one of its choice" in {

    val b = Coproduct[ISB](true)
    val i = Coproduct[ISB](3)

    coProductPrism[ISB, Int].getMaybe(i) ==== Maybe.just(3)
    coProductPrism[ISB, Int].getMaybe(b) ==== Maybe.empty

    coProductPrism[ISB, Boolean].getMaybe(i) ==== Maybe.empty
    coProductPrism[ISB, Boolean].getMaybe(b) ==== Maybe.just(true)

    coProductPrism[ISB, Boolean].reverseGet(true) ==== b

  }

  "coProductPrism can only create Prism to one of the type of the Coproduct" in {

    new AnyValueAsResult[Unit].asResult(
      illTyped("""coProductPrism[ISB, Float]""")
    )

  }

}
