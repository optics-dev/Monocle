package monocle.function

import monocle.TestUtil._
import monocle.Traversal
import monocle.function.Tail._
import org.specs2.scalaz.Spec


class TailSpec extends Spec {

  checkAll("tail List"   , Traversal.laws(tail[List[Int]]))
  checkAll("tail Stream" , Traversal.laws(tail[Stream[Int]]))
  checkAll("tail String" , Traversal.laws(tail[String]))

}
