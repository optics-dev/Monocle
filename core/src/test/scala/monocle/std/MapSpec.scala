package monocle.std

import monocle.Lens
import monocle.TestHelper._
import monocle.std.map._
import org.specs2.scalaz.Spec

class MapSpec extends Spec {

  implicit val mapEq = defaultEqual[Map[Int, String]]
  implicit val optEq = defaultEqual[Option[String]]

  checkAll(Lens.laws(at[Int, String](1)))

}
