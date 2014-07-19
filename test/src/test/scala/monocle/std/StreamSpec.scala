package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{IsoLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

class StreamSpec extends Spec {

  checkAll("each Stream", TraversalLaws(each[Stream[Int], Int]))

  checkAll("index Vector", OptionalLaws(index[Vector[String], Int, String](2)))

  checkAll("filterIndex Stream", TraversalLaws(filterIndex[Stream[Char], Int, Char](_ % 2 == 0)))

  checkAll("headOption Vector", OptionalLaws(headOption[Vector[Int], Int]))

  checkAll("tailOption Stream", OptionalLaws(tailOption[Stream[Int], Stream[Int]]))

  checkAll("lastOption Stream", OptionalLaws(lastOption[Stream[Int], Int]))

  checkAll("initOption Stream", OptionalLaws(initOption[Stream[Int], Stream[Int]]))

  checkAll("reverse Stream", IsoLaws(reverse[Stream[Int],Stream[Int]]))

}
