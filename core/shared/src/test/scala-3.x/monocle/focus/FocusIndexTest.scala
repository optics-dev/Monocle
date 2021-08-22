package monocle.focus

import monocle.Focus
import monocle.function.Index._

final class FocusIndexTest extends munit.FunSuite {

  test("Get direct `index` on the argument") {
    val index0 = Focus[List[Int]](_.index(0))
    val list   = List(1, 10, 100, 1000)

    assertEquals(index0.getOption(list), Some(1))
    assertEquals(index0.getOption(Nil), None)
  }

  test("Set direct `index` on the argument") {
    val index3 = Focus[List[Int]](_.index(3))
    val list   = List(5, 4, -44, 2)

    assertEquals(index3.replace(777)(list), List(5, 4, -44, 777))
    assertEquals(index3.replace(777)(List(88)), List(88))
  }

  test("Focus operator `index` commutes with standalone operator `index`") {
    val list = List("Melbourne", "Sydney", "Brisbane")

    assertEquals(Focus[List[String]](_.index(1)).getOption(list), Focus[List[String]](a => a).index(1).getOption(list))
  }
}
