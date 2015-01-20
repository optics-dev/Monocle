package monocle

import monocle.macros.GenIso
import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

class IsoExample extends Spec {

  case class Euro(value: Int)
  val euroIso = GenIso[Euro, Int]

  "macro Iso get" in {
    euroIso.get(Euro(5)) ==== 5
  }

  "macro Iso reverseGet" in {
    euroIso.reverseGet(5) ==== Euro(5)
  }
  
  case class Point(_x: Int, _y: Int)

  val pointToPair = Iso{l: Point => (l._x, l._y) }((Point.apply _).tupled)

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
