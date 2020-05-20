package monocle.generic

import monocle.Lens
import monocle.macros.GenLens
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

  test("coProductEitherIso creates an Iso between a Coproduct and the sum of its parts") {
    val i = Coproduct[ISB](3)
    val s = Coproduct[ISB]("hello")
    val b = Coproduct[ISB](true)

    coProductEitherIso[ISB].apply.get(i) shouldEqual Left(3)
    coProductEitherIso[ISB].apply.get(s) shouldEqual Right(Left("hello"))
    coProductEitherIso[ISB].apply.get(b) shouldEqual Right(Right(true))

    coProductEitherIso[ISB].apply.reverseGet(Left(3)) shouldEqual i
    coProductEitherIso[ISB].apply.reverseGet(Right(Left("hello"))) shouldEqual s
    coProductEitherIso[ISB].apply.reverseGet(Right(Right(true))) shouldEqual b
  }

  test("coProductToEither creates an Iso between a sealed trait and the sum of its parts") {
    sealed trait S
    case class A(name: String)      extends S
    case class B(name: String)      extends S
    case class C(otherName: String) extends S

    coProductToEither[S].apply.get(A("a")) shouldEqual Left(A("a"))
    coProductToEither[S].apply.get(B("b")) shouldEqual Right(Left(B("b")))
    coProductToEither[S].apply.get(C("c")) shouldEqual Right(Right(C("c")))

    coProductToEither[S].apply.reverseGet(Left(A("a"))) shouldEqual A("a")
    coProductToEither[S].apply.reverseGet(Right(Left(B("b")))) shouldEqual B("b")
    coProductToEither[S].apply.reverseGet(Right(Right(C("c")))) shouldEqual C("c")
  }

  test("coProductDisjunctionIso creates an Iso between a Coproduct and the sum of its parts") {
    val i = Coproduct[ISB](3)
    val s = Coproduct[ISB]("hello")
    val b = Coproduct[ISB](true)

    coProductDisjunctionIso[ISB].apply.get(i) shouldEqual Left(3)
    coProductDisjunctionIso[ISB].apply.get(s) shouldEqual Right(Left("hello"))
    coProductDisjunctionIso[ISB].apply.get(b) shouldEqual Right(Right(true))

    coProductDisjunctionIso[ISB].apply.reverseGet(Left(3)) shouldEqual i
    coProductDisjunctionIso[ISB].apply.reverseGet(Right(Left("hello"))) shouldEqual s
    coProductDisjunctionIso[ISB].apply.reverseGet(Right(Right(true))) shouldEqual b
  }

  test("coProductToDisjunction creates an Iso between a sealed trait and the sum of its parts") {
    sealed trait S
    case class A(name: String)      extends S
    case class B(name: String)      extends S
    case class C(otherName: String) extends S

    coProductToDisjunction[S].apply.get(A("a")) shouldEqual Left(A("a"))
    coProductToDisjunction[S].apply.get(B("b")) shouldEqual Right(Left(B("b")))
    coProductToDisjunction[S].apply.get(C("c")) shouldEqual Right(Right(C("c")))

    coProductToDisjunction[S].apply.reverseGet(Left(A("a"))) shouldEqual A("a")
    coProductToDisjunction[S].apply.reverseGet(Right(Left(B("b")))) shouldEqual B("b")
    coProductToDisjunction[S].apply.reverseGet(Right(Right(C("c")))) shouldEqual C("c")
  }

  test("coProductToDisjunction can be used to zoom in on a sealed trait's classes.") {
    sealed trait S
    case class A(name: String)      extends S
    case class B(name: String)      extends S
    case class C(otherName: String) extends S

    val lens: Lens[S, String] =
      coProductToDisjunction[S].apply composeLens
        (GenLens[A](_.name) choice (GenLens[B](_.name) choice GenLens[C](_.otherName)))

    lens.get(A("a")) shouldEqual "a"
    lens.get(B("b")) shouldEqual "b"
    lens.get(C("c")) shouldEqual "c"
  }
}
