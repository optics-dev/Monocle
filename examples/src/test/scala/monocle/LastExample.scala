package monocle

import monocle.function.Last._
import monocle.std.option._
import monocle.syntax.lens._
import org.specs2.scalaz.Spec


class LastExample extends Spec {

  "last creates a Lens from a List to its optional last element" in {
    (List(1,2,3)      |-> last get) shouldEqual Some(3)
    ((Nil: List[Int]) |-> last get) shouldEqual None

    (List(1,2,3) |-> last set Some(0)) shouldEqual List(1,2,0)

    // delete last
    (List(1,2,3) |-> last set None)    shouldEqual List(1,2)

    // add last
    ((Nil: List[Int]) |-> last set Some(1)) shouldEqual List(1)
  }

  "last creates a Lens from a Stream to its optional last element" in {
    (Stream(1,2,3) |-> last get) shouldEqual Some(3)

    (Stream(1,2,3) |-> last |->> some modify (_ + 1)) shouldEqual Stream(1,2,4)
  }

}
