package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{IsoLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

class ListSpec extends Spec {

  checkAll("each List", TraversalLaws(each[List[Int], Int]))

  checkAll("index List", OptionalLaws(index[List[String], Int, String](2)))

  checkAll("filterIndex List", TraversalLaws(filterIndex[List[Char], Int, Char](_ % 2 == 0)))

  checkAll("headOption List", OptionalLaws(headOption[List[Int], Int]))

  checkAll("tailOption List", OptionalLaws(tailOption[List[Int], List[Int]]))

  checkAll("lastOption List", OptionalLaws(lastOption[List[Int], Int]))

  checkAll("initOption List", OptionalLaws(initOption[List[Int], List[Int]]))

  checkAll("reverse List", IsoLaws(reverse[List[Int], List[Int]]))

}
