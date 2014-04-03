package monocle.util

import monocle.TestUtil._
import monocle.Traversal
import monocle.util.Each._
import org.specs2.scalaz.Spec
import scalaz.Equal

class EachSpec extends Spec {

  implicit val mapEq = Equal.equalA[Map[Int, String]]

  checkAll("each map", Traversal.laws(each[Map[Int, String], String]))

  checkAll("each option", Traversal.laws(each[Option[Int], Int]))

}
