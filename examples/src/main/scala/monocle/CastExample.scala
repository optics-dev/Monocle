package monocle

import monocle.std.char._
import monocle.std.int._

object CastExample extends App {

  println( intToChar.getOption(65) )   // Some(A)
  println( intToChar.reverseGet('a') ) // 97

  import monocle.syntax.prism._

  println( 65 <-? intToChar getOption ) // Some(A)

  println( "12345" <-? stringToInt getOption ) // Some(12345)
  println( "1ff1" <-? stringToInt getOption ) // None
  println( "Wrong number" <-? stringToInt getOption ) // None
}
