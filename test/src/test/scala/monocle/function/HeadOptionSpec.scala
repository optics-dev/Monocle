package monocle.function

import monocle.OptionalLaws
import monocle.TestUtil._
import monocle.function.HeadOption._
import org.specs2.scalaz.Spec


class HeadOptionSpec extends Spec {

  checkAll("head List"  , OptionalLaws(headOption[List[Int]  , Int]))
  checkAll("head Stream", OptionalLaws(headOption[Stream[Int], Int]))
  checkAll("head String", OptionalLaws(headOption[String     , Char]))
  checkAll("head Vector", OptionalLaws(headOption[Vector[Int], Int]))
  checkAll("head Stream", OptionalLaws(headOption[Stream[Int], Int]))

}
