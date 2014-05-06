package monocle.function

import monocle.TestUtil._
import monocle.TraversalLaws
import monocle.function.Index._
import org.specs2.scalaz.Spec


class IndexSpec extends Spec {

  checkAll("index Map"   , TraversalLaws(index[Map[Int, String], Int, String](3)))
  checkAll("index List"  , TraversalLaws(index[List[String], Int, String](2)))
  checkAll("index String", TraversalLaws(index[String, Int, Char](2)))
  checkAll("index Vector", TraversalLaws(index[Vector[String], Int, String](2)))
  checkAll("index Stream", TraversalLaws(index[Stream[String], Int, String](2)))
}
