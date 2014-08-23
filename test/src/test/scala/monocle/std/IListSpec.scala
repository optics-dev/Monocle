package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{PrismLaws, IsoLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

import scalaz.IList


class IListSpec extends Spec {

  checkAll("cons - snoc IList", ConsSnocLaws[IList[Char], Char])

  checkAll("each IList", TraversalLaws(each[IList[Int], Int]))

  checkAll("filterIndex IList", TraversalLaws(filterIndex[IList[Char], Int, Char](_ % 2 == 0)))

  checkAll("headOption IList", OptionalLaws(headOption[IList[Int], Int]))

  checkAll("index IList", OptionalLaws(index[IList[String], Int, String](2)))

  checkAll("initOption IList", OptionalLaws(initOption[IList[Int], IList[Int]]))

  checkAll("lastOption IList", OptionalLaws(lastOption[IList[Int], Int]))

  checkAll("reverse IList", IsoLaws(reverse[IList[Int], IList[Int]]))

  checkAll("tailOption IList", OptionalLaws(tailOption[IList[Int], IList[Int]]))

}
