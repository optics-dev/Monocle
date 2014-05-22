package monocle.function

import monocle.TestUtil._
import monocle.TraversalLaws
import monocle.function.LastOption._
import org.specs2.scalaz.Spec


class LastOptionSpec extends Spec {

  checkAll("last List"  , TraversalLaws(lastOption[List[Int]  , Int]))
  checkAll("last Stream", TraversalLaws(lastOption[Stream[Int], Int]))
  checkAll("last String", TraversalLaws(lastOption[String     , Char]))
  checkAll("last Vector", TraversalLaws(lastOption[Vector[Int], Int]))

}
