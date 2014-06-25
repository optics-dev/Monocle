package monocle.function

import monocle.OptionalLaws
import monocle.TestUtil._
import monocle.function.HeadOption._
import org.specs2.scalaz.Spec
import scalaz.IList


class HeadOptionSpec extends Spec {

  checkAll("headOption List"  , OptionalLaws(headOption[List[Int]  , Int]))
  checkAll("headOption IList" , OptionalLaws(headOption[IList[Int] , Int]))
  checkAll("headOption Stream", OptionalLaws(headOption[Stream[Int], Int]))
  checkAll("headOption String", OptionalLaws(headOption[String     , Char]))
  checkAll("headOption Vector", OptionalLaws(headOption[Vector[Int], Int]))
  checkAll("headOption Stream", OptionalLaws(headOption[Stream[Int], Int]))

}
