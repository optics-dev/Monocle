package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.{IsoTests, TraversalTests}
import monocle.law.discipline.function._

import scalaz.Cofree
import scalaz.Cofree._
import scalaz.std.option._

class CofreeSpec extends MonocleSuite {
  // TODO: need Arbitrary and Equal instances for Cofree
  // checkAll("pCofreeToStream", IsoTests(pCofreeToStream[Int, Int]))
  // checkAll("pCofreeToTree", IsoTests(pCofreeToTree[Int, Int]))
  // checkAll("cons cofree", ConsTests[Cofree[Option, Int], Int])
  // checkAll("each cofree", EachTests[Cofree[Option, Int], Int])
}

