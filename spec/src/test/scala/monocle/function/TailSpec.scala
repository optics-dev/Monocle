package monocle.function

import monocle.TestUtil._
import monocle.TraversalLaws
import monocle.function.Tail._
import org.specs2.scalaz.Spec


class TailSpec extends Spec {

  checkAll("tail List"   , TraversalLaws(tail[List[Int]]))
  checkAll("tail Stream" , TraversalLaws(tail[Stream[Int]]))
  checkAll("tail String" , TraversalLaws(tail[String]))
  checkAll("tail Vector" , TraversalLaws(tail[Vector[Int]]))
  checkAll("tail Stream" , TraversalLaws(tail[Stream[Int]]))

}
