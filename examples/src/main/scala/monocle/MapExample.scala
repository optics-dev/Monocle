package monocle

import monocle.std.map._

object MapExample extends App {

  val map = Map(1 -> "One", 2 -> "Two")

  val atFirst = at[Int, String](1)

  println( atFirst.get(map) ) // Some(One)

  println( atFirst.set(map, None) ) // Map(2 -> "Two") i.e.

  println( atFirst.set(map, Some("Un")) ) // replace One by Un

  // with some syntax sugar

  import monocle.syntax.lens._
  import monocle.std.option._

  println( map |-> at(1) |->> some modify(_.reverse) ) // Map(1 -> enO, 2 -> Two)

}
