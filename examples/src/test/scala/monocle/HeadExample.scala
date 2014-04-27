package monocle

import monocle.function.Head._
import monocle.syntax.traversal._
import org.specs2.scalaz.Spec


class HeadExample extends Spec {

  "head creates a Traversal from a List, Stream or Vector to its optional first element" in {
    (List(1,2,3)      |->> head headOption) shouldEqual Some(1)
    (Stream(1,2,3)    |->> head headOption) shouldEqual Some(1)
    (Vector(1,2,3)    |->> head headOption) shouldEqual Some(1)


    (List.empty[Int]  |->> head headOption)    shouldEqual None
    (List.empty[Int]  |->> head modify(_ + 1)) shouldEqual Nil

    (List(1,2,3)      |->> head set 0) shouldEqual List(0,2,3)
  }

  "head creates a Traversal from a String to its optional head Char" in {
    ("Hello" |->> head headOption) shouldEqual Some('H')

    ("Hello" |->> head set 'M') shouldEqual "Mello"
  }

  "head creates a Traversal from an Option to its optional element" in {
    (Option(1)          |->> head headOption) shouldEqual Some(1)
    ((None: Option[Int])|->> head headOption) shouldEqual None
  }

}
