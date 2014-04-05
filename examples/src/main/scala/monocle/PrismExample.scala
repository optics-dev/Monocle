package monocle

import monocle.std.option._
import monocle.thirdparty.either
import either._
import scalaz.{ \/-, -\/ }

object PrismExample extends App {

  println( left.getOption(-\/(1)) )   // Some(1)
  println( left.getOption(\/-(1)) )   // None

  println( right.getOption(-\/(1)) )  // None
  println( right.getOption(\/-(1)) )  // Some(1)

}
