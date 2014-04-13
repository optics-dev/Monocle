package monocle.function

import monocle.TestUtil._
import monocle.Traversal
import monocle.function.Last._
import org.specs2.scalaz.Spec


class LastSpec extends Spec {

  checkAll("last List"  , Traversal.laws(last[List[Int]  , Int]))
  checkAll("last Stream", Traversal.laws(last[Stream[Int], Int]))
  checkAll("last String", Traversal.laws(last[String     , Char]))

}
