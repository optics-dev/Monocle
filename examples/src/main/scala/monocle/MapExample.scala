package monocle

import monocle.std.map._

object MapExample extends App {

  val map = Map(1 -> "One", 2 -> "Two")

  val atFirst = at[Int, String](1)

  println(atFirst.get(map)) // => Some(One)

  println(atFirst.set(map, None)) // delete 1

  println(atFirst.set(map, Some("Un"))) // replace One by Un

}
