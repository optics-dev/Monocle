package monocle

import monocle.std.char._
import monocle.syntax.prism._
import org.specs2.scalaz.Spec

class SafeCastExample extends Spec {

  "an intToChar is a Prism from Int to Char" in {
    intToChar.getOption(65)   shouldEqual Some('A')
    intToChar.reverseGet('a') shouldEqual 97

    // with some syntax sugar
    (65 <-? intToChar getOption) shouldEqual Some('A')
  }

}
