package monocle

import monocle.law.discipline.{OptionalTests, SetterTests, TraversalTests}
import cats.arrow.{Category, Choice, Compose}
import monocle.macros.GenLens

class OptionalSpec extends MonocleSuite {
  def headOption[A]: Optional[List[A], A] =
    Optional[List[A], A](_.headOption) { a =>
      {
        case x :: xs => a :: xs
        case Nil     => Nil
      }
    }

  def headOptionI: Optional[List[Int], Int]             = headOption[Int]
  def headOption2[A, B]: Optional[List[(A, B)], (A, B)] = headOption[(A, B)]

  checkAll("apply Optional", OptionalTests(headOptionI))

  checkAll("optional.asTraversal", TraversalTests(headOptionI.asTraversal))
  checkAll("optional.asSetter", SetterTests(headOptionI.asSetter))

  checkAll("first", OptionalTests(headOptionI.first[Boolean]))
  checkAll("second", OptionalTests(headOptionI.second[Boolean]))

  test("void") {
    assertEquals(Optional.void.getOption("hello"), None)
    assertEquals(Optional.void.replace(5)("hello"), "hello")
  }

  // test implicit resolution of type classes

  test("Optional has a Compose instance") {
    assertEquals(
      Compose[Optional]
        .compose(headOptionI, headOption[List[Int]])
        .getOption(List(List(1, 2, 3), List(4))),
      Some(1)
    )
  }

  test("Optional has a Category instance") {
    assertEquals(Category[Optional].id[Int].getOption(3), Some(3))
  }

  test("Optional has a Choice instance") {
    assertEquals(
      Choice[Optional]
        .choice(headOptionI, Category[Optional].id[Int])
        .getOption(Left(List(1, 2, 3))),
      Some(1)
    )
  }

  test("getOption") {
    assertEquals(headOptionI.getOption(List(1, 2, 3, 4)), Some(1))
    assertEquals(headOptionI.getOption(Nil), None)
  }

  test("isEmpty") {
    assertEquals(headOptionI.isEmpty(List(1, 2, 3, 4)), false)
    assertEquals(headOptionI.isEmpty(Nil), true)
  }

  test("nonEmpty") {
    assertEquals(headOptionI.nonEmpty(List(1, 2, 3, 4)), true)
    assertEquals(headOptionI.nonEmpty(Nil), false)
  }

  test("find") {
    assertEquals(headOptionI.find(_ > 0)(List(1, 2, 3, 4)), Some(1))
    assertEquals(headOptionI.find(_ > 9)(List(1, 2, 3, 4)), None)
  }

  test("exist") {
    assertEquals(headOptionI.exist(_ > 0)(List(1, 2, 3, 4)), true)
    assertEquals(headOptionI.exist(_ > 9)(List(1, 2, 3, 4)), false)
    assertEquals(headOptionI.exist(_ > 9)(Nil), false)
  }

  test("all") {
    assertEquals(headOptionI.all(_ > 2)(List(1, 2, 3, 4)), false)
    assertEquals(headOptionI.all(_ > 0)(List(1, 2, 3, 4)), true)
    assertEquals(headOptionI.all(_ > 0)(Nil), true)
  }

  test("set") {
    assertEquals(headOptionI.replace(0)(List(1, 2, 3, 4)), List(0, 2, 3, 4))
    assertEquals(headOptionI.replace(0)(Nil), Nil)
  }

  test("setOption") {
    assertEquals(headOptionI.setOption(0)(List(1, 2, 3, 4)), Some(List(0, 2, 3, 4)))
    assertEquals(headOptionI.setOption(0)(Nil), None)
  }

  test("modify") {
    assertEquals(headOptionI.modify(_ + 1)(List(1, 2, 3, 4)), List(2, 2, 3, 4))
    assertEquals(headOptionI.modify(_ + 1)(Nil), Nil)
  }

  test("modifyOption") {
    assertEquals(headOptionI.modifyOption(_ + 1)(List(1, 2, 3, 4)), Some(List(2, 2, 3, 4)))
    assertEquals(headOptionI.modifyOption(_ + 1)(Nil), None)
  }

  test("to") {
    assertEquals(headOptionI.to(_.toString()).getAll(List(1, 2, 3)), List("1"))
  }

  test("some") {
    case class SomeTest(x: Int, y: Option[Int])
    val obj = SomeTest(1, Some(2))

    val optional = GenLens[SomeTest](_.y).asOptional

    assertEquals(optional.some.getOption(obj), Some(2))
    assertEquals(obj.applyOptional(optional).some.getOption, Some(2))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val optional = GenLens[SomeTest](_.y).asOptional

    assertEquals(optional.withDefault(0).getOption(objSome), Some(2))
    assertEquals(optional.withDefault(0).getOption(objNone), Some(0))

    assertEquals(objNone.applyOptional(optional).withDefault(0).getOption, Some(0))
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val optional = GenLens[SomeTest](_.y).asOptional

    assertEquals(optional.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.applyOptional(optional).each.getAll, List(1, 2, 3))
  }

  test("at") {
    case class SomeTest(x: Int, y: (String, Int))
    val obj = SomeTest(1, ("one", 1))

    val optional = GenLens[SomeTest](_.y).asOptional

    assertEquals(optional.at(1).getOption(obj), Some("one"))
    assertEquals(obj.applyOptional(optional).at(1).getOption, Some("one"))
  }
}
