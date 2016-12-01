package monocle.example

import monocle.Traversal

object TraversalExample {

  case class Point(id: String, x: Int, y: Int)

  val points = Traversal.apply2[Point, Int](_.x, _.y)((x, y, p) => p.copy(x = x, y = y))

}
