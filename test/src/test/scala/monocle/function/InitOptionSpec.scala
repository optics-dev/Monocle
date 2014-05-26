package monocle.function

import monocle.OptionalLaws
import monocle.TestUtil._
import monocle.function.InitOption._
import org.specs2.scalaz.Spec

class InitOptionSpec extends Spec {

  checkAll("initOption List"  , OptionalLaws(initOption[List[Int]  , List[Int]]))
  checkAll("initOption Stream", OptionalLaws(initOption[Stream[Int], Stream[Int]]))
  checkAll("initOption Vector", OptionalLaws(initOption[Vector[Int], Vector[Int]]))
  checkAll("initOption Stream", OptionalLaws(initOption[Stream[Int], Stream[Int]]))

  checkAll("initOption String", OptionalLaws(initOption[String     , String]))

}

