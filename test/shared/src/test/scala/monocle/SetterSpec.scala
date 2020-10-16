package monocle

import cats.arrow.{Category, Choice, Compose}
import monocle.macros.GenLens

class SetterSpec extends MonocleSuite {
  def eachL[A]: Setter[List[A], A] = PSetter.fromFunctor[List, A, A]
  def even[A]: Setter[List[A], A] =
    filterIndex[List[A], Int, A](_ % 2 == 0).asSetter

  def eachLi: Setter[List[Int], Int]             = eachL[Int]
  def eachL2[A, B]: Setter[List[(A, B)], (A, B)] = eachL[(A, B)]

  // test implicit resolution of type classes

  test("Setter has a Compose instance") {
    Compose[Setter]
      .compose(eachL[Int], eachL[List[Int]])
      .set(3)(List(List(1, 2, 3), List(4))) shouldEqual List(
      List(3, 3, 3),
      List(3)
    )
  }

  test("Setter has a Category instance") {
    Category[Setter].id[Int].modify(_ + 1)(3) shouldEqual 4
  }

  test("Setter has a Choice instance") {
    Choice[Setter]
      .choice(eachL[Int], even[Int])
      .modify(_ + 1)(Right(List(1, 2, 3, 4))) shouldEqual Right(
      List(2, 2, 4, 4)
    )
  }

  test("set") {
    eachLi.set(0)(List(1, 2, 3, 4)) shouldEqual List(0, 0, 0, 0)
  }

  test("modify") {
    eachLi.modify(_ + 1)(List(1, 2, 3, 4)) shouldEqual List(2, 3, 4, 5)
  }

  test("some") {
    case class SomeTest(x: Int, y: Option[Int])
    val obj = SomeTest(1, Some(2))

    val setter = GenLens[SomeTest](_.y).asSetter

    setter.some.set(3)(obj) shouldEqual SomeTest(1, Some(3))
    obj.applySetter(setter).some.set(3) shouldEqual SomeTest(1, Some(3))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val setter = GenLens[SomeTest](_.y).asSetter

    setter.withDefault(0).modify(_ + 1)(objSome) shouldEqual SomeTest(1, Some(3))
    setter.withDefault(0).modify(_ + 1)(objNone) shouldEqual SomeTest(1, Some(1))

    objNone.applySetter(setter).withDefault(0).modify(_ + 1) shouldEqual SomeTest(1, Some(1))
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val setter = GenLens[SomeTest](_.y).asSetter

    setter.each.set(3)(obj) shouldEqual SomeTest(1, List(3, 3, 3))
    obj.applySetter(setter).each.set(3) shouldEqual SomeTest(1, List(3, 3, 3))
  }
}
