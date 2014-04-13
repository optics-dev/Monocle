package monocle

import monocle.function.Last._
import monocle.syntax.traversal._
import org.specs2.scalaz.Spec


class LastExample extends Spec {

  "last creates a Lens from a List to its optional last element" in {
    (List(1,2,3)      |->> last headOption) shouldEqual Some(3)
    ((Nil: List[Int]) |->> last headOption) shouldEqual None

    (List(1,2,3) |->> last set 0) shouldEqual List(1,2,0)
  }

  "last creates a Lens from a List to its optional last element" in {
    (Stream(1,2,3) |->> last headOption) shouldEqual Some(3)

    (Stream(1,2,3) |->> last set 0) shouldEqual Stream(1,2,0)
  }

  "last creates a Lens from a String to its optional last Char" in {
    ("Hello" |->> last headOption) shouldEqual Some('o')

    ("Hello" |->> last set 'a') shouldEqual "Hella"
  }

}
