package monocle

import org.specs2.scalaz.Spec
import monocle.function.Head._
import monocle.syntax.lens._
import monocle.std.option._


class HeadExample extends Spec {

  "head creates a Lens from a List to its optional first element" in {
    (List(1,2,3)      |-> head get) shouldEqual Some(1)
    ((Nil: List[Int]) |-> head get) shouldEqual None

    (List(1,2,3) |-> head set Some(0)) shouldEqual List(0,2,3)

    // delete head
    (List(1,2,3) |-> head set None)    shouldEqual List(2,3)

    // add head
    ((Nil: List[Int]) |-> head set Some(1)) shouldEqual List(1)
  }

  "head creates a Lens from a Stream to its optional first element" in {
    (Stream(1,2,3) |-> head get) shouldEqual Some(1)

    (Stream(1,2,3) |-> head |->> some modify (_ + 1)) shouldEqual Stream(2,2,3)
  }

}
