package monocle

import monocle.std.list._
import org.specs2.scalaz.Spec

class ListExample extends Spec {
  
  "head creates a Lens from a List to its headOption" in {

    head.get(List(1,2,3)) shouldEqual Some(1)
    head.get(Nil)         shouldEqual None

    head.set(List(1,2,3), Some(0)) shouldEqual List(0,2,3)
    // delete head
    head.set(List(1,2,3), None)    shouldEqual List(2,3)
    // add head
    head.set(Nil, Some(1))         shouldEqual List(1)

  }

  "tail creates a Lens from a List to its tailOption in " in {

    last.get(List(1,2,3)) shouldEqual Some(3)
    last.get(List()) shouldEqual None
    last.set(List(1,2,3), Some(1)) shouldEqual List(1,2,1)
    last.set(Nil, Some(1)) shouldEqual List(1)


  }

}
