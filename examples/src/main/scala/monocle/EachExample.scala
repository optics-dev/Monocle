package monocle

import monocle.Traversal.each
import monocle.std.map._
import monocle.std.option._
import monocle.syntax.traversal._

object EachExample extends App {

  println( Option(3) |->> each modify( _ + 1) )

  println( Map("One" -> 1, "Two" -> 2) |->> each modify( _ + 1) )

}
