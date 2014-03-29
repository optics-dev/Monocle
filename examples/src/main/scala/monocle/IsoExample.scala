package monocle

import monocle.std.tuple._

object IsoExample extends App {
  
  case class Point(_x: Int, _y: Int)
  val x = Macro.mkLens[Point, Int]("_x")
  

  val pointToPair = SimpleIso[Point, (Int, Int)](
    { l => (l._x, l._y) },
    { case (_x, _y) => Point(_x, _y) }
  )

  val point = Point(3, 5)
  val tuple = (3, 5)

  println( pointToPair.set(point, (4, 6)) ) // Point(4, 6)
  println( pointToPair.reverse compose x modify (tuple, _ + 1) ) // (4, 5)

  import monocle.syntax.iso._

  // here we use lenses and traversal on 2-tuple instead of creating ad-hoc ones for Point
  println( Point(3, 4) <-> pointToPair |-> _1 get ) // 3
  println( Point(3, 4) <-> pointToPair |->> both modify(_ + 1) ) // Point(4, 5)

}
