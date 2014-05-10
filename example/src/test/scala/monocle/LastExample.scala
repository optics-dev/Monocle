package monocle

import monocle.function.Last._
import monocle.syntax._
import org.specs2.scalaz.Spec
import scalaz.IList


class LastExample extends Spec {

  "last creates a Traversal from a List, IList, Stream or Vector to its optional first element" in {
    (List(1,2,3)      |->> last headOption) shouldEqual Some(3)
    (IList(1,2,3)     |->> last headOption) shouldEqual Some(3)
    (Stream(1,2,3)    |->> last headOption) shouldEqual Some(3)
    (Vector(1,2,3)    |->> last headOption) shouldEqual Some(3)

    (List.empty[Int]  |->> last headOption)    shouldEqual None
    (List.empty[Int]  |->> last modify(_ + 1)) shouldEqual Nil

    (List(1,2,3)      |->> last set 0) shouldEqual List(1,2,0)
  }

  "last creates a Traversal from a String to its optional head Char" in {
    ("Hello" |->> last headOption) shouldEqual Some('o')

    ("Hello" |->> last set 'a') shouldEqual "Hella"
  }

  "last creates a Traversal from an Option to its optional element" in {
    (Option(1)          |->> last headOption) shouldEqual Some(1)
    ((None: Option[Int])|->> last headOption) shouldEqual None
  }

}
