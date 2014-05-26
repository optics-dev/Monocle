package monocle.function

import monocle.OptionalLaws
import monocle.TestUtil._
import monocle.function.TailOption._
import org.specs2.scalaz.Spec


class TailOptionSpec extends Spec {

  checkAll("tailOption List"  , OptionalLaws(tailOption[List[Int]  , List[Int]]))
  checkAll("tailOption Stream", OptionalLaws(tailOption[Stream[Int], Stream[Int]]))
  checkAll("tailOption Vector", OptionalLaws(tailOption[Vector[Int], Vector[Int]]))
  checkAll("tailOption Stream", OptionalLaws(tailOption[Stream[Int], Stream[Int]]))

  checkAll("tailOption String", OptionalLaws(tailOption[String     , String]))

}
