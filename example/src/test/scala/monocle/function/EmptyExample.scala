package monocle.function

import monocle.MonocleSuite
import monocle.function.all.{empty => mempty}
import scala.collection.immutable.SortedMap

class EmptyExample extends MonocleSuite {

  test("empty is a Prism that is successful only when S is empty") {
    (List(1, 2, 3) applyPrism mempty getOption) shouldEqual None

    (List.empty[Int]   applyPrism mempty getOption) shouldEqual Some(())
    (Vector.empty[Int] applyPrism mempty getOption) shouldEqual Some(())
    (""                applyPrism mempty getOption) shouldEqual Some(())
  }

  test("_empty return the empty value of a given type") {
    _empty[List[Int]]        shouldEqual List.empty[Int]
    _empty[SortedMap[Int, String]] shouldEqual SortedMap.empty[Int, String]
    _empty[String]           shouldEqual ""
  }

  test("_isEmpty is a function that takes an S and return true is S is empty, false otherwise") {
    _isEmpty(List(1,2,3)) shouldEqual false
    _isEmpty("hello")     shouldEqual false

    _isEmpty(List.empty)  shouldEqual true
    _isEmpty("")          shouldEqual true
  }

}
