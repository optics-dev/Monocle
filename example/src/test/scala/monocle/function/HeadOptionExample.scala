package monocle.function

import monocle.MonocleSuite
import monocle.std._
import monocle.syntax._

import scalaz.IList

class HeadOptionExample extends MonocleSuite {

  test("headOption creates a Traversal from a List, Stream or Vector to its optional first element") {
    (List(1,2,3)     applyOptional headOption getOption) shouldEqual Some(1)
    (Stream(1,2,3)   applyOptional headOption getOption) shouldEqual Some(1)
    (Vector(1,2,3)   applyOptional headOption getOption) shouldEqual Some(1)
    (IList(1,2,3)    applyOptional headOption getOption) shouldEqual Some(1)

    (List.empty[Int] applyOptional headOption getOption)     shouldEqual None
    (List.empty[Int] applyOptional headOption modify(_ + 1)) shouldEqual Nil

    (List(1,2,3)     applyOptional headOption set 0)       shouldEqual List(0,2,3)
    (List(1,2,3)     applyOptional headOption setOption 0) shouldEqual Some(List(0,2,3))

    (List.empty[Int] applyOptional headOption set 0)       shouldEqual Nil
    (List.empty[Int] applyOptional headOption setOption 0) shouldEqual None

  }

  test("headOption creates a Traversal from a String to its optional head Char") {
    ("Hello" applyOptional headOption getOption) shouldEqual Some('H')

    ("Hello" applyOptional headOption set 'M') shouldEqual "Mello"
  }

}
