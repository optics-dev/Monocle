package monocle.function

import monocle.TestUtil._
import monocle.Traversal
import monocle.function.Init._
import org.specs2.scalaz.Spec

class InitSpec extends Spec {

  checkAll("init List"  , Traversal.laws(init[List[Int]]))
  checkAll("init Stream", Traversal.laws(init[Stream[Int]]))
  checkAll("init String", Traversal.laws(init[String]))

}

