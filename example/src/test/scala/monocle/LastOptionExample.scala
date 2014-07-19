package monocle

import monocle.function.LastOption._
import monocle.std._
import monocle.scalazi._
import monocle.syntax._
import org.specs2.scalaz.Spec
import scalaz.{OneAnd, IList}


class LastOptionExample extends Spec {

  "lastOption creates a Traversal from a List, IList, Stream, Vector or OneAnd to its optional last element" in {
    (List(1,2,3)           |-? lastOption getOption) shouldEqual Some(3)
    (IList(1,2,3)          |-? lastOption getOption) shouldEqual Some(3)
    (Stream(1,2,3)         |-? lastOption getOption) shouldEqual Some(3)
    (Vector(1,2,3)         |-? lastOption getOption) shouldEqual Some(3)
    (OneAnd(1, List(2, 3)) |-? lastOption getOption) shouldEqual Some(3)

    (List.empty[Int] |-? lastOption getOption)    shouldEqual None
    (List.empty[Int] |-? lastOption modify(_ + 1)) shouldEqual Nil

    (List(1,2,3)     |-? lastOption set 0) shouldEqual List(1,2,0)
  }

  "lastOption creates a Traversal from a String to its optional last Char" in {
    ("Hello" |-? lastOption getOption) shouldEqual Some('o')

    ("Hello" |-? lastOption set 'a') shouldEqual "Hella"
  }

  "lastOption creates a Traversal from an Option to its optional element" in {
    (Some(1)            |-? lastOption getOption) shouldEqual Some(1)
    ((None: Option[Int])|-? lastOption getOption) shouldEqual None
  }

}
