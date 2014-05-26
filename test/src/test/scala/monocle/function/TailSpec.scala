package monocle.function

import monocle.OptionalLaws
import monocle.TestUtil._
import monocle.function.Tail._
import org.specs2.scalaz.Spec


class TailSpec extends Spec {

  checkAll("tail List"  , OptionalLaws(tail[List[Int]  , List[Int]]))
  checkAll("tail Stream", OptionalLaws(tail[Stream[Int], Stream[Int]]))
  checkAll("tail Vector", OptionalLaws(tail[Vector[Int], Vector[Int]]))
  checkAll("tail Stream", OptionalLaws(tail[Stream[Int], Stream[Int]]))

  checkAll("tail String", OptionalLaws(tail[String     , String]))

}
