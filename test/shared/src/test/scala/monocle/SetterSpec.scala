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
    assertEquals(
      Compose[Setter]
        .compose(eachL[Int], eachL[List[Int]])
        .replace(3)(List(List(1, 2, 3), List(4))),
      List(List(3, 3, 3), List(3))
    )
  }

  test("Setter has a Category instance") {
    assertEquals(Category[Setter].id[Int].modify(_ + 1)(3), 4)
  }

  test("Setter has a Choice instance") {
    assertEquals(
      Choice[Setter]
        .choice(eachL[Int], even[Int])
        .modify(_ + 1)(Right(List(1, 2, 3, 4))),
      Right(List(2, 2, 4, 4))
    )
  }

  test("set") {
    assertEquals(eachLi.replace(0)(List(1, 2, 3, 4)), List(0, 0, 0, 0))
  }

  test("modify") {
    assertEquals(eachLi.modify(_ + 1)(List(1, 2, 3, 4)), List(2, 3, 4, 5))
  }

  test("some") {
    case class SomeTest(x: Int, y: Option[Int])
    val obj = SomeTest(1, Some(2))

    val setter = GenLens[SomeTest](_.y).asSetter

    assertEquals(setter.some.replace(3)(obj), SomeTest(1, Some(3)))
    assertEquals(obj.applySetter(setter).some.replace(3), SomeTest(1, Some(3)))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val setter = GenLens[SomeTest](_.y).asSetter

    assertEquals(setter.withDefault(0).modify(_ + 1)(objSome), SomeTest(1, Some(3)))
    assertEquals(setter.withDefault(0).modify(_ + 1)(objNone), SomeTest(1, Some(1)))

    assertEquals(objNone.applySetter(setter).withDefault(0).modify(_ + 1), SomeTest(1, Some(1)))
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val setter = GenLens[SomeTest](_.y).asSetter

    assertEquals(setter.each.replace(3)(obj), SomeTest(1, List(3, 3, 3)))
    assertEquals(obj.applySetter(setter).each.replace(3), SomeTest(1, List(3, 3, 3)))
  }
}
