package monocle.focus

import monocle.Focus
import monocle.function.At._

final class FocusAtTest extends munit.FunSuite {

  test("Get direct `at` on the argument") {
    val atA = Focus[Map[String,Int]](_.at("a"))
    val map = Map("a" -> 1, "b" -> 2, "c" -> 3)

    assertEquals(atA.get(map), Some(1))
    assertEquals(atA.get(Map()), None)
  }

  test("Set direct `at` on the argument") {
    val atB = Focus[Map[String,Int]](_.at("b"))
    val map = Map("a" -> 1, "b" -> 2, "c" -> 3)

    assertEquals(atB.replace(Some(55))(map), map.updated("b", 55))
    assertEquals(atB.replace(None)(map), map - "b")
  }

  test("Get/set on tuples") {
    val at3 = Focus[(Int,Int,Int)](_.at(3))

    val tup = (2, 3, 4)

    assertEquals(at3.get(tup), 4)
    assertEquals(at3.replace(55)(tup), (2, 3, 55))
  }

  test("Focus operator `at` commutes with standalone operator `at`") {
    type PeopleMap = Map[String,Int]
    val map: PeopleMap = Map("Bob" -> 44, "Sue" -> 21, "Etienne" -> 33)

    assertEquals(
      Focus[PeopleMap](_.at("Bob")).get(map), 
      Focus[PeopleMap](a => a).at("Bob").get(map))
  }
}
