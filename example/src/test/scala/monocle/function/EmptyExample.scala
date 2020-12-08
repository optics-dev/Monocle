package monocle.function

import monocle.MonocleSuite
import monocle.function.all.{empty => mempty}

import scala.annotation.nowarn

@nowarn
class EmptyExample extends MonocleSuite {
  test("empty is a Prism that is successful only when S is empty") {
    assertEquals((List(1, 2, 3) applyPrism mempty getOption), None)

    assertEquals((List.empty[Int] applyPrism mempty getOption), Some(()))
    assertEquals((Vector.empty[Int] applyPrism mempty getOption), Some(()))
    assertEquals(("" applyPrism mempty getOption), Some(()))
  }

  test("_empty return the empty value of a given type") {
    assertEquals(_empty[List[Int]], List.empty[Int])
    assertEquals(_empty[Map[Int, String]], Map.empty[Int, String])
    assertEquals(_empty[String], "")
  }

  test("_isEmpty is a function that takes an S and return true is S is empty, false otherwise") {
    assertEquals(_isEmpty(List(1, 2, 3)), false)
    assertEquals(_isEmpty("hello"), false)

    assertEquals(_isEmpty(List.empty), true)
    assertEquals(_isEmpty(""), true)
  }
}
