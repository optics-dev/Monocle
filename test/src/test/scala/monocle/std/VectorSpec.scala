package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{PrismLaws, IsoLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

class VectorSpec extends Spec {

  checkAll("cons - snoc Vector", ConsSnocLaws[Vector[Char], Char])

  checkAll("each Vector", TraversalLaws(each[Vector[Int], Int]))

  checkAll("filterIndex Vector", TraversalLaws(filterIndex[Vector[Char], Int, Char](_ % 2 == 0)))

  checkAll("headOption Vector", OptionalLaws(headOption[Vector[Int], Int]))

  checkAll("index Vector", OptionalLaws(index[Vector[String], Int, String](2)))

  checkAll("initOption Vector", OptionalLaws(initOption[Vector[Int], Vector[Int]]))

  checkAll("lastOption Vector", OptionalLaws(lastOption[Vector[Int], Int]))

  checkAll("reverse Vector", IsoLaws(_reverse[Vector[Int], Vector[Int]]))

  checkAll("snoc Vector", PrismLaws(_snoc[Vector[Char], Char]))

  checkAll("tailOption Vector", OptionalLaws(tailOption[Vector[Int], Vector[Int]]))

}
