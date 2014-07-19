package monocle

import monocle.function.SafeCast._
import monocle.generic.coproduct._
import org.specs2.execute.AnyValueAsResult
import org.specs2.scalaz.Spec
import shapeless.test.illTyped
import shapeless.{Coproduct, CNil, :+:}


class CoproductExample extends Spec {

  type ISB = Int :+: String :+: Boolean :+: CNil


  "safeCast creates a Prism between a Coproduct and one of its choice" in {

    val b = Coproduct[ISB](true)
    val i = Coproduct[ISB](3)

    safeCast[ISB, Int].getOption(i) shouldEqual Some(3)
    safeCast[ISB, Int].getOption(b) shouldEqual None

    safeCast[ISB, Boolean].getOption(i) shouldEqual None
    safeCast[ISB, Boolean].getOption(b) shouldEqual Some(true)

    safeCast[ISB, Boolean].reverseGet(true) shouldEqual b

  }

  "safeCast can only create Prism to one of the type of the Coproduct" in {

    new AnyValueAsResult[Unit].asResult(
      illTyped("""safeCast[ISB, Float]""")
    )

  }

}
