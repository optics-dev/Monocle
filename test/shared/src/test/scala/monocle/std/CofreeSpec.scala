package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.IsoTests
import monocle.law.discipline.function._

import scalaz.Cofree
import scalaz.std.option.optionInstance

class CofreeSpec extends MonocleSuite {
   checkAll("cofreeToStream", IsoTests(cofreeToStream[Int]))
   checkAll("cofreeToTree", IsoTests(cofreeToTree[Int]))
   checkAll("cons1 cofree", Cons1Tests[Cofree[Option, Int], Int, Option[Cofree[Option, Int]]])
   checkAll("each cofree", EachTests[Cofree[Option, Int], Int])
}

