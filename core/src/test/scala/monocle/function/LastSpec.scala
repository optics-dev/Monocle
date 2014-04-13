package monocle.function

import monocle.Lens
import monocle.TestUtil._
import monocle.function.Last._
import org.specs2.scalaz.Spec


class LastSpec extends Spec {

  checkAll("last List"  , Lens.laws(last[List[Int]  , Int]))
  checkAll("last Stream", Lens.laws(last[Stream[Int], Int]))
  checkAll("last String", Lens.laws(last[String     , Char]))


}
