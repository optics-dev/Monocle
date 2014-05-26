package monocle.function

import monocle.TestUtil._
import monocle.OptionalLaws
import monocle.function.LastOption._
import org.specs2.scalaz.Spec


class LastOptionSpec extends Spec {

  checkAll("last List"  , OptionalLaws(lastOption[List[Int]  , Int]))
  checkAll("last Stream", OptionalLaws(lastOption[Stream[Int], Int]))
  checkAll("last String", OptionalLaws(lastOption[String     , Char]))
  checkAll("last Vector", OptionalLaws(lastOption[Vector[Int], Int]))

}
