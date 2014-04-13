package monocle

import monocle.std.list._
import org.specs2.scalaz.Spec

class ListExample extends Spec {

  "last creates a Lens from a List to its tailOption in " in {

    last.get(List(1,2,3)) shouldEqual Some(3)
    last.get(List()) shouldEqual None
    last.set(List(1,2,3), Some(1)) shouldEqual List(1,2,1)
    last.set(Nil, Some(1)) shouldEqual List(1)

  }

}
