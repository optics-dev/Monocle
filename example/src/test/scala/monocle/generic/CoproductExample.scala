package monocle.generic

import monocle.Lens
import monocle.macros.GenLens
import monocle.MonocleSuite
import shapeless.test.illTyped
import shapeless.{:+:, CNil, Coproduct}

import scala.annotation.nowarn

@nowarn
class CoproductExample extends MonocleSuite with GenericInstances {
  type ISB = Int :+: String :+: Boolean :+: CNil

  test("coProductPrism creates a Prism between a Coproduct and one of its choice") {
    val b = Coproduct[ISB](true)
    val i = Coproduct[ISB](3)

    assertEquals(coProductPrism[ISB, Int].getOption(i), Some(3))
    assertEquals(coProductPrism[ISB, Int].getOption(b), None)

    assertEquals(coProductPrism[ISB, Boolean].getOption(i), None)
    assertEquals(coProductPrism[ISB, Boolean].getOption(b), Some(true))

    assertEquals(coProductPrism[ISB, Boolean].reverseGet(true), b)
  }

  test("coProductPrism can only create Prism to one of the type of the Coproduct") {
    illTyped("""coProductPrism[ISB, Float]""")
  }

  test("coProductEitherIso creates an Iso between a Coproduct and the sum of its parts") {
    val i = Coproduct[ISB](3)
    val s = Coproduct[ISB]("hello")
    val b = Coproduct[ISB](true)

    assertEquals(coProductEitherIso[ISB].apply.get(i), Left(3))
    assertEquals(coProductEitherIso[ISB].apply.get(s), Right(Left("hello")))
    assertEquals(coProductEitherIso[ISB].apply.get(b), Right(Right(true)))

    assertEquals(coProductEitherIso[ISB].apply.reverseGet(Left(3)), i)
    assertEquals(coProductEitherIso[ISB].apply.reverseGet(Right(Left("hello"))), s)
    assertEquals(coProductEitherIso[ISB].apply.reverseGet(Right(Right(true))), b)
  }

  test("coProductToEither creates an Iso between a sealed trait and the sum of its parts") {
    sealed trait S
    case class A(name: String)      extends S
    case class B(name: String)      extends S
    case class C(otherName: String) extends S

    assertEquals(coProductToEither[S].apply.get(A("a")), Left(A("a")))
    assertEquals(coProductToEither[S].apply.get(B("b")), Right(Left(B("b"))))
    assertEquals(coProductToEither[S].apply.get(C("c")), Right(Right(C("c"))))

    assertEquals(coProductToEither[S].apply.reverseGet(Left(A("a"))), A("a"))
    assertEquals(coProductToEither[S].apply.reverseGet(Right(Left(B("b")))), B("b"))
    assertEquals(coProductToEither[S].apply.reverseGet(Right(Right(C("c")))), C("c"))
  }

  test("coProductDisjunctionIso creates an Iso between a Coproduct and the sum of its parts") {
    val i = Coproduct[ISB](3)
    val s = Coproduct[ISB]("hello")
    val b = Coproduct[ISB](true)

    assertEquals(coProductDisjunctionIso[ISB].apply.get(i), Left(3))
    assertEquals(coProductDisjunctionIso[ISB].apply.get(s), Right(Left("hello")))
    assertEquals(coProductDisjunctionIso[ISB].apply.get(b), Right(Right(true)))

    assertEquals(coProductDisjunctionIso[ISB].apply.reverseGet(Left(3)), i)
    assertEquals(coProductDisjunctionIso[ISB].apply.reverseGet(Right(Left("hello"))), s)
    assertEquals(coProductDisjunctionIso[ISB].apply.reverseGet(Right(Right(true))), b)
  }

  test("coProductToDisjunction creates an Iso between a sealed trait and the sum of its parts") {
    sealed trait S
    case class A(name: String)      extends S
    case class B(name: String)      extends S
    case class C(otherName: String) extends S

    assertEquals(coProductToDisjunction[S].apply.get(A("a")), Left(A("a")))
    assertEquals(coProductToDisjunction[S].apply.get(B("b")), Right(Left(B("b"))))
    assertEquals(coProductToDisjunction[S].apply.get(C("c")), Right(Right(C("c"))))

    assertEquals(coProductToDisjunction[S].apply.reverseGet(Left(A("a"))), A("a"))
    assertEquals(coProductToDisjunction[S].apply.reverseGet(Right(Left(B("b")))), B("b"))
    assertEquals(coProductToDisjunction[S].apply.reverseGet(Right(Right(C("c")))), C("c"))
  }

  test("coProductToDisjunction can be used to zoom in on a sealed trait's classes.") {
    sealed trait S
    case class A(name: String)      extends S
    case class B(name: String)      extends S
    case class C(otherName: String) extends S

    val lens: Lens[S, String] =
      coProductToDisjunction[S].apply andThen
        GenLens[A](_.name).choice(GenLens[B](_.name).choice(GenLens[C](_.otherName)))

    assertEquals(lens.get(A("a")), "a")
    assertEquals(lens.get(B("b")), "b")
    assertEquals(lens.get(C("c")), "c")
  }
}
