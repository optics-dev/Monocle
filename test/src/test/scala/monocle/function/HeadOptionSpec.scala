package monocle.function

import monocle.TestUtil._
import monocle.TraversalLaws
import monocle.function.HeadOption._
import org.specs2.scalaz.Spec


class HeadOptionSpec extends Spec {

  checkAll("head List"  , TraversalLaws(headOption[List[Int]  , Int]))
  checkAll("head Stream", TraversalLaws(headOption[Stream[Int], Int]))
  checkAll("head String", TraversalLaws(headOption[String     , Char]))
  checkAll("head Vector", TraversalLaws(headOption[Vector[Int], Int]))
  checkAll("head Stream", TraversalLaws(headOption[Stream[Int], Int]))

}
