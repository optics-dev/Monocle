package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{IsoLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

import _root_.scalaz.IList


class IListSpec extends Spec {

  checkAll("each IList", TraversalLaws(each[IList[Int], Int]))

  checkAll("index IList", OptionalLaws(index[IList[String], Int, String](2)))

  checkAll("filterIndex IList", TraversalLaws(filterIndex[IList[Char], Int, Char](_ % 2 == 0)))

  checkAll("headOption IList", OptionalLaws(headOption[IList[Int], Int]))

  checkAll("tailOption IList", OptionalLaws(tailOption[IList[Int], IList[Int]]))

  checkAll("lastOption IList", OptionalLaws(lastOption[IList[Int], Int]))

  checkAll("initOption IList", OptionalLaws(initOption[IList[Int], IList[Int]]))

  checkAll("reverse IList", IsoLaws(reverse[IList[Int], IList[Int]]))

}
