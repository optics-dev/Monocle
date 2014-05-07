package monocle.function

import monocle.TestUtil._
import monocle.TraversalLaws
import monocle.function.Last._
import org.specs2.scalaz.Spec


class LastSpec extends Spec {

  checkAll("last List"  , TraversalLaws(last[List[Int]  , Int]))
  checkAll("last Stream", TraversalLaws(last[Stream[Int], Int]))
  checkAll("last String", TraversalLaws(last[String     , Char]))
  checkAll("last Vector", TraversalLaws(last[Vector[Int], Int]))

}
