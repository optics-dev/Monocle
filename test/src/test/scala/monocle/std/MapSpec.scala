package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

class MapSpec extends Spec {

  checkAll("at Map", TraversalLaws(at[Map[Int, String], Int, String](2)))

  checkAll("index Map", OptionalLaws(index[Map[Int, String], Int, String](3)))

  checkAll("filterIndex Map", TraversalLaws(filterIndex[Map[Int, Char], Int, Char](_ % 2 == 0)))

  checkAll("each Map", TraversalLaws(each[Map[Int, String], String]))

}
