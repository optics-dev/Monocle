package monocle

import org.specs2.scalaz.Spec
import monocle.syntax._
import monocle.std._

class IsoExample extends Spec {
  
  case class Point(_x: Int, _y: Int)

  val pointToPair = SimpleIso[Point, (Int, Int)](
    { l => (l._x, l._y) },
    { case (_x, _y) => Point(_x, _y) }
  )

  "Iso get transforms a S into an A" in {
    (Point(3, 5) ^<-> pointToPair get) shouldEqual (3, 5)
  }

  "Iso reverse reverses the transformation" in {
    ((3, 5) ^<-> pointToPair.reverse get) shouldEqual Point(3, 5)
  }

  "Iso composition can limit the need of ad-hoc Lens" in {
    import monocle.function.Field1._

    // here we use tuple Lens on Pair via pointToPair Iso
    (Point(3, 5) ^<-> pointToPair |-> first get)   shouldEqual 3
    (Point(3, 5) ^<-> pointToPair |-> first set 4) shouldEqual Point(4, 5)
  }

}
