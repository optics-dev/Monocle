package monocle.function

import monocle.TestUtil._
import monocle.TraversalLaws
import monocle.function.Init._
import org.specs2.scalaz.Spec

class InitSpec extends Spec {

  checkAll("init List"  , TraversalLaws(init[List[Int]]))
  checkAll("init Stream", TraversalLaws(init[Stream[Int]]))
  checkAll("init String", TraversalLaws(init[String]))
  checkAll("init Vector", TraversalLaws(init[Vector[Int]]))
  checkAll("init Stream", TraversalLaws(init[Stream[Int]]))

}

