package monocle.std

import monocle.TestUtil._
import monocle.std.map._
import monocle.{Traversal, Lens}
import org.specs2.scalaz.Spec
import scalaz.Equal

class MapSpec extends Spec {

  implicit val mapEq = Equal.equalA[Map[Int, String]]

  checkAll("at", Lens.laws(at[Int, String](1)))

  checkAll("each map", Traversal.laws(Traversal.each[Map[Int, String], String]))

}
