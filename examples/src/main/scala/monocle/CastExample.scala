package monocle

import monocle.std.char._

object CastExample extends App {

  println(intToChar.getOption(65)) // Some(A)
  println(intToChar.reverseGet('a')) // 97

}
