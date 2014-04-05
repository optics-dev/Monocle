package monocle

import monocle.std.list._

object ListExample extends App {

  println(head.get(List(1,2,3))) // Some(1)
  println(head.set(List(1,2,3), Some(0))) // List(0,2,3)
  println(head.set(List(1,2,3), None)) // List(2,3)
  println(head.set(Nil, Some(2))) // List(2)
}
