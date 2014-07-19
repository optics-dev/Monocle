package monocle

import monocle.function.HeadOption._
import monocle.std._
import monocle.scalazi.ilist._
import monocle.syntax._
import org.specs2.scalaz.Spec
import scalaz.IList


class HeadOptionExample extends Spec {

  "headOption creates a Traversal from a List, Stream or Vector to its optional first element" in {
    (List(1,2,3)     |-? headOption getOption) shouldEqual Some(1)
    (Stream(1,2,3)   |-? headOption getOption) shouldEqual Some(1)
    (Vector(1,2,3)   |-? headOption getOption) shouldEqual Some(1)
    (IList(1,2,3)    |-? headOption getOption) shouldEqual Some(1)

    (List.empty[Int] |-? headOption getOption)     shouldEqual None
    (List.empty[Int] |-? headOption modify(_ + 1)) shouldEqual Nil

    (List(1,2,3)     |-? headOption set 0)       shouldEqual List(0,2,3)
    (List(1,2,3)     |-? headOption setOption 0) shouldEqual Some(List(0,2,3))

    (List.empty[Int] |-? headOption set 0)       shouldEqual Nil
    (List.empty[Int] |-? headOption setOption 0) shouldEqual None

  }

  "headOption creates a Traversal from a String to its optional head Char" in {
    ("Hello" |-? headOption getOption) shouldEqual Some('H')

    ("Hello" |-? headOption set 'M') shouldEqual "Mello"
  }

  "headOption creates a Traversal from an Option to its optional element" in {
    (Some(1)            |-? headOption getOption) shouldEqual Some(1)
    ((None: Option[Int])|-? headOption getOption) shouldEqual None
  }

}
