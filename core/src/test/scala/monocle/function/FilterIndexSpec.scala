package monocle.function

import org.specs2.scalaz.Spec
import monocle.Traversal
import monocle.function.FilterIndex._
import monocle.TestUtil._


class FilterIndexSpec extends Spec {

  def predicate(i: Int): Boolean = i%2 == 0

  checkAll("filterIndex Map"   , Traversal.laws(filterIndex[Map[Int, Char], Int, Char](predicate)))

  checkAll("filterIndex List"  , Traversal.laws(filterIndex[List[Char], Int, Char](predicate)))

  checkAll("filterIndex String", Traversal.laws(filterIndex[String, Int, Char](predicate)))

  checkAll("filterIndex Vector", Traversal.laws(filterIndex[Vector[Char], Int, Char](predicate)))
}
