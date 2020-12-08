package monocle.std

import cats.data.Chain
import monocle.MonocleSuite
import monocle.function.Plated._
import monocle.law.discipline.{IsoTests, TraversalTests}
import monocle.law.discipline.function._

import scala.annotation.nowarn

class ChainSpec extends MonocleSuite {
  import cats.laws.discipline.arbitrary._

  checkAll("chainToList", IsoTests(chainToList[Int]))
  checkAll("chainToVector", IsoTests(chainToVector[Int]))

  checkAll("reverse Chain", ReverseTests[Chain[Int]]): @nowarn
  checkAll("empty Chain", EmptyTests[Chain[Int]]): @nowarn
  checkAll("cons Chain", ConsTests[Chain[Int], Int])
  checkAll("snoc Chain", SnocTests[Chain[Int], Int])
  checkAll("each Chain", EachTests[Chain[Int], Int])
  checkAll("index Chain", IndexTests[Chain[Int], Int, Int])
  checkAll("filterIndex Chain", FilterIndexTests[Chain[Int], Int, Int])

  checkAll("plated Chain", TraversalTests(plate[Chain[Int]]))
}
