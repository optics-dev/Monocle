package monocle

import org.specs2.scalaz.Spec


class SyntaxExample extends Spec {

  case class Address(streetNumber: Int, streetName: String)
  case class Person(name: String, address: Address)

  val addressL      = SimpleLens[Person](_.address)((p, a) => p.copy(address = a))
  val streetNumberL = SimpleLens[Address](_.streetNumber)((a, n) => a.copy(streetNumber = n))

  val robert = Person("Robert", Address(24, "Birch Grove"))

  "syntax permits to use optic as an operator" in {
    // normal usage
    addressL composeLens streetNumberL get robert shouldEqual 24

    // operator usage
    import monocle.syntax._
    (robert applyLens addressL composeLens streetNumberL get) shouldEqual 24
  }

  "syntax permits to use symbols instead of composeX and applyX" in {
    import monocle.syntax._
    val composedLens: SimpleLens[Person, Int] = addressL |-> streetNumberL

    (robert ^|-> addressL |-> streetNumberL get) shouldEqual 24
  }

}
