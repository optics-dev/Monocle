package monocle.function

import monocle.Lens
import monocle.TestUtil._
import monocle.function.Head._
import org.specs2.scalaz.Spec


class HeadSpec extends Spec {

  checkAll("head List"  , Lens.laws(head[List[Int]  , Int]))
  checkAll("head Stream", Lens.laws(head[Stream[Int], Int]))
  checkAll("last String", Lens.laws(head[String     , Char]))

}
