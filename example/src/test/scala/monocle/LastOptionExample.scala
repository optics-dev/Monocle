package monocle

import monocle.function.LastOption._
import monocle.syntax.traversal._
import org.specs2.scalaz.Spec
import scalaz.IList


class LastOptionExample extends Spec {

  "lastOption creates a Traversal from a List, IList, Stream or Vector to its optional first element" in {
    (List(1,2,3)      |->> lastOption headOption) shouldEqual Some(3)
    (IList(1,2,3)     |->> lastOption headOption) shouldEqual Some(3)
    (Stream(1,2,3)    |->> lastOption headOption) shouldEqual Some(3)
    (Vector(1,2,3)    |->> lastOption headOption) shouldEqual Some(3)

    (List.empty[Int]  |->> lastOption headOption)    shouldEqual None
    (List.empty[Int]  |->> lastOption modify(_ + 1)) shouldEqual Nil

    (List(1,2,3)      |->> lastOption set 0) shouldEqual List(1,2,0)
  }

  "lastOption creates a Traversal from a String to its optional head Char" in {
    ("Hello" |->> lastOption headOption) shouldEqual Some('o')

    ("Hello" |->> lastOption set 'a') shouldEqual "Hella"
  }

  "lastOption creates a Traversal from an Option to its optional element" in {
    (Option(1)          |->> lastOption headOption) shouldEqual Some(1)
    ((None: Option[Int])|->> lastOption headOption) shouldEqual None
  }

}
