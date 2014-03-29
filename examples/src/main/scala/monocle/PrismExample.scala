package monocle

import monocle.std.option._
import monocle.thirdparty.either
import either._
import scalaz.{ \/-, -\/ }

object PrismExample extends App {

  println( some.getOption(Some(1)) ) // Some(1)
  println( some.set(None, 2) )       // None
  println( some.set(Some(1), 'a') )  // Some('a')
  println( some.modify(Some(1), { n: Int => n + 2.0 }) ) // Some(3.0)

  println( none.getOption(Some(1)) ) // None
  println( none.getOption(None) )    // Some(())

  println( left.getOption(-\/(1)) )   // Some(1)
  println( left.getOption(\/-(1)) )   // None

  println( right.getOption(-\/(1)) )  // None
  println( right.getOption(\/-(1)) )  // Some(1)

}
