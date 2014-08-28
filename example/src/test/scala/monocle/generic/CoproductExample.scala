package monocle.generic

import monocle.function.SafeCast._
import org.specs2.execute.AnyValueAsResult
import org.specs2.scalaz.Spec
import shapeless.test.illTyped
import shapeless.{:+:, CNil, Coproduct}

import scalaz.Maybe


class CoproductExample extends Spec {

  type ISB = Int :+: String :+: Boolean :+: CNil


  "safeCast creates a Prism between a Coproduct and one of its choice" in {

    val b = Coproduct[ISB](true)
    val i = Coproduct[ISB](3)

    safeCast[ISB, Int].getMaybe(i) ==== Maybe.just(3)
    safeCast[ISB, Int].getMaybe(b) ==== Maybe.empty

    safeCast[ISB, Boolean].getMaybe(i) ==== Maybe.empty
    safeCast[ISB, Boolean].getMaybe(b) ==== Maybe.just(true)

    safeCast[ISB, Boolean].reverseGet(true) ==== b

  }

  "safeCast can only create Prism to one of the type of the Coproduct" in {

    new AnyValueAsResult[Unit].asResult(
      illTyped("""safeCast[ISB, Float]""")
    )

  }

}
