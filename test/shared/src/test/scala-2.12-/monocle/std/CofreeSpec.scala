package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.IsoTests
import monocle.law.discipline.function._

import cats.free.Cofree

class CofreeSpec extends MonocleSuite {
  import cats.laws.discipline.arbitrary._

  checkAll("cofreeToStream", IsoTests(cofreeToStream[Int]))
  checkAll("cons1 cofree", Cons1Tests[Cofree[Option, Int], Int, Option[Cofree[Option, Int]]])
  checkAll("each cofree", EachTests[Cofree[Option, Int], Int])
}
