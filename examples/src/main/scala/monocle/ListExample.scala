package monocle

import monocle.std.list._

object ListExample extends App {

  head.get(List(1,2,3)) == Some(1)
  head.set(List(1,2,3), Some(0)) == List(0,2,3)
  head.set(List(1,2,3), None) == List(2,3) // delete
  head.set(Nil, Some(2)) == Nil
}
