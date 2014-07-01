package monocle.function

import monocle.OptionalLaws
import monocle.TestUtil._
import monocle.function.Index._
import org.specs2.scalaz.Spec
import scalaz.OneAnd


class IndexSpec extends Spec {

  checkAll("index Map"   , OptionalLaws(index[Map[Int, String], Int, String](3)))
  checkAll("index List"  , OptionalLaws(index[List[String], Int, String](2)))
  checkAll("index String", OptionalLaws(index[String, Int, Char](2)))
  checkAll("index Vector", OptionalLaws(index[Vector[String], Int, String](2)))
  checkAll("index Stream", OptionalLaws(index[Stream[String], Int, String](2)))

  checkAll("index OneAnd", OptionalLaws(index[OneAnd[List, Int], Int, Int](1)))
}
