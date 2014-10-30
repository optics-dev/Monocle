package monocle

import org.specs2.scalaz.Spec
import monocle.syntax._
import monocle.std._

class IsoExample extends Spec {
  
  case class Point(_x: Int, _y: Int)

  val pointToPair = SimpleIso{l: Point => (l._x, l._y) }((Point.apply _).tupled)

  "Iso get transforms a S into an A" in {
    (Point(3, 5) applyIso pointToPair get) ==== ((3, 5))
  }

  "Iso reverse reverses the transformation" in {
    ((3, 5) applyIso pointToPair.reverse get) ==== Point(3, 5)
  }

  "Iso composition can limit the need of ad-hoc Lens" in {
    import monocle.function.Field1._

    // here we use tuple Lens on Pair via pointToPair Iso
    (Point(3, 5) applyIso pointToPair composeLens first get)   ==== 3
    (Point(3, 5) applyIso pointToPair composeLens first set 4) ==== Point(4, 5)
  }

}
