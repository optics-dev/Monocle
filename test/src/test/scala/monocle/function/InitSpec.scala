package monocle.function

import monocle.OptionalLaws
import monocle.TestUtil._
import monocle.function.Init._
import org.specs2.scalaz.Spec

class InitSpec extends Spec {

  checkAll("init List"  , OptionalLaws(init[List[Int]  , List[Int]]))
  checkAll("init Stream", OptionalLaws(init[Stream[Int], Stream[Int]]))
  checkAll("init Vector", OptionalLaws(init[Vector[Int], Vector[Int]]))
  checkAll("init Stream", OptionalLaws(init[Stream[Int], Stream[Int]]))

  checkAll("init String", OptionalLaws(init[String     , String]))

}

