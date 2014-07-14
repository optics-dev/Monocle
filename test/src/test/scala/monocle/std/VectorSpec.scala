package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{IsoLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

class VectorSpec extends Spec {

  checkAll("each Vector", TraversalLaws(each[Vector[Int], Int]))

  checkAll("index Vector", OptionalLaws(index[Vector[String], Int, String](2)))

  checkAll("filterIndex Vector", TraversalLaws(filterIndex[Vector[Char], Int, Char](_ % 2 == 0)))

  checkAll("headOption Vector", OptionalLaws(headOption[Vector[Int], Int]))

  checkAll("tailOption Vector", OptionalLaws(tailOption[Vector[Int], Vector[Int]]))

  checkAll("lastOption Vector", OptionalLaws(lastOption[Vector[Int], Int]))

  checkAll("initOption Vector", OptionalLaws(initOption[Vector[Int], Vector[Int]]))

  checkAll("reverse Vector", IsoLaws(reverse[Vector[Int], Vector[Int]]))

}
