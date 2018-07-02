package monocle.generic

import monocle.Lens
import monocle.macros.GenLens
import monocle.MonocleSuite
import shapeless.test.illTyped
import shapeless.{:+:, CNil, Coproduct}
import scalaz.{-\/, \/-}

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

  test("coProductIso creates an Iso between a Coproduct and the sum of its parts") {
    val i = Coproduct[ISB](3)
    val s = Coproduct[ISB]("hello")
    val b = Coproduct[ISB](true)

    coProductIso[ISB].apply.get(i) shouldEqual Left(3)
    coProductIso[ISB].apply.get(s) shouldEqual Right(Left("hello"))
    coProductIso[ISB].apply.get(b) shouldEqual Right(Right(true))

    coProductIso[ISB].apply.reverseGet(Left(3)) shouldEqual i
    coProductIso[ISB].apply.reverseGet(Right(Left("hello"))) shouldEqual s
    coProductIso[ISB].apply.reverseGet(Right(Right(true))) shouldEqual b
  }

  test("coProductDisjunctionIso creates an Iso between a Coproduct and the sum of its parts") {
    val i = Coproduct[ISB](3)
    val s = Coproduct[ISB]("hello")
    val b = Coproduct[ISB](true)

    coProductDisjunctionIso[ISB].apply.get(i) shouldEqual -\/(3)
    coProductDisjunctionIso[ISB].apply.get(s) shouldEqual \/-(-\/("hello"))
    coProductDisjunctionIso[ISB].apply.get(b) shouldEqual \/-(\/-(true))

    coProductDisjunctionIso[ISB].apply.reverseGet(-\/(3)) shouldEqual i
    coProductDisjunctionIso[ISB].apply.reverseGet(\/-(-\/("hello"))) shouldEqual s
    coProductDisjunctionIso[ISB].apply.reverseGet(\/-(\/-(true))) shouldEqual b
  }

  test("coProductDisjunctionIso can be composed with toGeneric") {
    sealed trait X
    case class A(a: String) extends X
    case class B(b: String) extends X
    case class C(c: String) extends X

    val lens: Lens[X, String] =
      toGeneric[X] composeIso coProductDisjunctionIso.apply composeLens
        (GenLens[A](_.a) choice (GenLens[B](_.b) choice GenLens[C](_.c)))

    lens.get(A("a")) shouldEqual "a"
    lens.get(B("b")) shouldEqual "b"
    lens.get(C("c")) shouldEqual "c"
  }
}
