package monocle

import monocle.TestUtil._
import monocle.function.Each._
import monocle.syntax.traversal._
import org.specs2.scalaz.{ScalazMatchers, Spec}
import scalaz.Tree._


class EachExample extends Spec with ScalazMatchers {

  "Each can be used on Option" in {
    Option(3)            |->> each modify( _ + 1) shouldEqual Some(4)
    (None : Option[Int]) |->> each modify( _ + 1) shouldEqual None
  }

  "Each can be used on List, Vector and Stream" in {
    List(1,2)    |->> each modify( _ + 1) shouldEqual List(2,3)
    Stream(1,2)  |->> each modify( _ + 1) shouldEqual Stream(2,3)
    Vector(1,2)  |->> each modify( _ + 1) shouldEqual Vector(2,3)
  }

  "Each can be used on Map to update all values" in {
    Map("One" -> 1, "Two" -> 2) |->> each modify( _ + 1) shouldEqual Map("One" -> 2, "Two" -> 3)
  }

  "Each can be used on tuple of same type" in {
    (1, 2)             |->> each modify( _ + 1) shouldEqual (2, 3)
    (1, 2, 3)          |->> each modify( _ + 1) shouldEqual (2, 3, 4)
    (1, 2, 3, 4, 5, 6) |->> each modify( _ + 1) shouldEqual (2, 3, 4, 5, 6, 7)
  }

  "Each can be used on Tree" in {
    node(1, Stream(leaf(2), leaf(3))) |->> each modify( _ + 1) must equal (node(2, Stream(leaf(3), leaf(4))))
    (node(1, Stream(leaf(2), leaf(3))) |->> each getAll) shouldEqual List(1,2,3)
  }

}
