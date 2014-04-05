package monocle.util

import monocle.TestUtil._
import monocle.Traversal
import monocle.util.Each._
import org.specs2.scalaz.Spec

class EachSpec extends Spec {

  checkAll("each map", Traversal.laws(each[Map[Int, String], String]))

  checkAll("each option", Traversal.laws(each[Option[Int], Int]))

  checkAll("each List", Traversal.laws(each[List[Int], Int]))

  checkAll("each triple", Traversal.laws(each[(Int, Int, Int), Int]))

}
