package monocle.std

import monocle.Lens
import monocle.TestHelper._
import org.specs2.scalaz.Spec

class MapSpec extends Spec {

  val at = Map.at[Int, String](1)

  implicit val mapEq = defaultEqual[Map[Int, String]]
  implicit val optEq = defaultEqual[Option[String]]

  checkAll(Lens.laws(at))

}
