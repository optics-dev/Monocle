package monocle.function

import monocle.TestUtil._
import monocle.TraversalLaws
import monocle.function.FilterIndex._
import org.specs2.scalaz.Spec

class FilterIndexSpec extends Spec {

  def predicate(i: Int): Boolean = i%2 == 0

  checkAll("filterIndex Map"   , TraversalLaws(filterIndex[Map[Int, Char], Int, Char](predicate)))
  checkAll("filterIndex List"  , TraversalLaws(filterIndex[List[Char], Int, Char](predicate)))
  checkAll("filterIndex String", TraversalLaws(filterIndex[String, Int, Char](predicate)))
  checkAll("filterIndex Vector", TraversalLaws(filterIndex[Vector[Char], Int, Char](predicate)))
  checkAll("filterIndex Stream", TraversalLaws(filterIndex[Stream[Char], Int, Char](predicate)))
}
