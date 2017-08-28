package monocle.function

import monocle.MonocleSuite

import scala.collection.immutable.{List => IList, Map => IMap}
import cats.data.OneAnd

class EachExample extends MonocleSuite {

  test("Each can be used on Option") {
    (Option(3)            applyTraversal each modify( _ + 1)) shouldEqual Some(4)
    ((None : Option[Int]) applyTraversal each modify( _ + 1)) shouldEqual None
  }

  test("Each can be used on List, IList, Vector, Stream and OneAnd") {
    (List(1,2)   applyTraversal each modify( _ + 1)) shouldEqual List(2,3)
    (IList(1,2)  applyTraversal each modify( _ + 1)) shouldEqual IList(2,3)
    (Stream(1,2) applyTraversal each modify( _ + 1)) shouldEqual Stream(2,3)
    (Vector(1,2) applyTraversal each modify( _ + 1)) shouldEqual Vector(2,3)
    (OneAnd(1, List(2,3)) applyTraversal each modify( _ + 1)) shouldEqual OneAnd(2, List(3,4))
  }

  test("Each can be used on Map, IMap to update all values") {
    (Map("One" -> 1, "Two" -> 2) applyTraversal each modify( _ + 1)) shouldEqual Map("One" -> 2, "Two" -> 3)
    (IMap("One" -> 1, "Two" -> 2) applyTraversal each modify( _ + 1)) shouldEqual IMap("One" -> 2, "Two" -> 3)
  }

  test("Each can be used on tuple of same type") {
    ((1, 2)             applyTraversal each modify( _ + 1)) shouldEqual ((2, 3))
    ((1, 2, 3)          applyTraversal each modify( _ + 1)) shouldEqual ((2, 3, 4))
    ((1, 2, 3, 4, 5, 6) applyTraversal each modify( _ + 1)) shouldEqual ((2, 3, 4, 5, 6, 7))
  }

}
