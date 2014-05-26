package monocle.function

import monocle.OptionalLaws
import monocle.TestUtil._
import monocle.function.TailOption._
import org.specs2.scalaz.Spec


class TailOptionSpec extends Spec {

  checkAll("tail List"  , OptionalLaws(tailOption[List[Int]  , List[Int]]))
  checkAll("tail Stream", OptionalLaws(tailOption[Stream[Int], Stream[Int]]))
  checkAll("tail Vector", OptionalLaws(tailOption[Vector[Int], Vector[Int]]))
  checkAll("tail Stream", OptionalLaws(tailOption[Stream[Int], Stream[Int]]))

  checkAll("tail String", OptionalLaws(tailOption[String     , String]))

}
