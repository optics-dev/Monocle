package monocle.function

import monocle.TestUtil._
import monocle.OptionalLaws
import monocle.function.LastOption._
import org.specs2.scalaz.Spec
import scalaz.OneAnd


class LastOptionSpec extends Spec {

  checkAll("lastOption List"  , OptionalLaws(lastOption[List[Int]  , Int]))
  checkAll("lastOption Stream", OptionalLaws(lastOption[Stream[Int], Int]))
  checkAll("lastOption String", OptionalLaws(lastOption[String     , Char]))
  checkAll("lastOption Vector", OptionalLaws(lastOption[Vector[Int], Int]))

  checkAll("lastOption OneAnd", OptionalLaws(lastOption[OneAnd[List, Int], Int]))

}
