package monocle

import monocle.macros.GenIso
import monocle.std._
import monocle.syntax._

class IsoExample extends MonocleSuite {

  case class Euro(value: Int)
  val euroIso = GenIso[Euro, Int]

  test("macro Iso get") {
    euroIso.get(Euro(5)) shouldEqual 5
  }

  test("macro Iso reverseGet") {
    euroIso.reverseGet(5) shouldEqual Euro(5)
  }
  
  case class Point(_x: Int, _y: Int)

  val pointToPair = Iso{l: Point => (l._x, l._y) }((Point.apply _).tupled)

  test("Iso get transforms a S into an A") {
    (Point(3, 5) applyIso pointToPair get) shouldEqual ((3, 5))
  }

  test("Iso reverse reverses the transformation") {
    ((3, 5) applyIso pointToPair.reverse get) shouldEqual Point(3, 5)
  }

  test("Iso composition can limit the need of ad-hoc Lens") {
    import monocle.function.Field1._

    // here we use tuple Lens on Pair via pointToPair Iso
    (Point(3, 5) applyIso pointToPair composeLens first get)   shouldEqual 3
    (Point(3, 5) applyIso pointToPair composeLens first set 4) shouldEqual Point(4, 5)
  }

}
