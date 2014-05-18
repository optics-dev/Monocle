package monocle

import monocle.function.HeadOption._
import monocle.syntax.traversal._
import org.specs2.scalaz.Spec
import scalaz.IList


class HeadOptionExample extends Spec {

  "headOption creates a Traversal from a List, Stream or Vector to its optional first element" in {
    (List(1,2,3)      |->> headOption headOption) shouldEqual Some(1)
    (Stream(1,2,3)    |->> headOption headOption) shouldEqual Some(1)
    (Vector(1,2,3)    |->> headOption headOption) shouldEqual Some(1)
    (IList(1,2,3)     |->> headOption headOption) shouldEqual Some(1)

    (List.empty[Int]  |->> headOption headOption)    shouldEqual None
    (List.empty[Int]  |->> headOption modify(_ + 1)) shouldEqual Nil

    (List(1,2,3)      |->> headOption set 0) shouldEqual List(0,2,3)
  }

  "headOption creates a Traversal from a String to its optional head Char" in {
    ("Hello" |->> headOption headOption) shouldEqual Some('H')

    ("Hello" |->> headOption set 'M') shouldEqual "Mello"
  }

  "headOption creates a Traversal from an Option to its optional element" in {
    (Option(1)          |->> headOption headOption) shouldEqual Some(1)
    ((None: Option[Int])|->> headOption headOption) shouldEqual None
  }

}
