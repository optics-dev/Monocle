package monocle

object IsoExample extends App {

  case class Point(_x: Int, _y: Int)
  val x = Macro.mkLens[Point, Int]("_x")

  val iso = SimpleIso[Point, (Int, Int)](
    { l => (l._x, l._y) },
    { case (_x, _y) => Point(_x, _y) })

  val point = Point(3, 5)
  val tuple = (3, 5)

  println(iso.set(point, (4, 6))) // Point(4, 6)
  println(iso.reverse compose x modify (tuple, _ + 1)) // (4, 5)

  import monocle.std.tuple._
  val xSynonym = iso compose _1[Int, Int, Int]

  println(xSynonym get Point(3, 4)) // 3
  println(xSynonym set (Point(3, 4), 4)) // Point(4, 4)

}
