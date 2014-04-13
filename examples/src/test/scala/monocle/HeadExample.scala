package monocle

import monocle.function.Head._
import monocle.syntax.traversal._
import org.specs2.scalaz.Spec


class HeadExample extends Spec {

  "head creates a Traversal from a List to its optional first element" in {
    (List(1,2,3)      |->> head headOption) shouldEqual Some(1)
    ((Nil: List[Int]) |->> head headOption) shouldEqual None

    (List(1,2,3) |->> head set 0) shouldEqual List(0,2,3)
  }

  "head creates a Traversal from a Stream to its optional first element" in {
    (Stream(1,2,3) |->> head headOption) shouldEqual Some(1)

    (Stream(1,2,3) |->> head modify (_ + 1)) shouldEqual Stream(2,2,3)
  }

  "head creates a Traversal from a String to its optional head Char" in {
    ("Hello" |->> head headOption) shouldEqual Some('H')

    ("Hello" |->> head set 'M') shouldEqual "Mello"
  }

}
