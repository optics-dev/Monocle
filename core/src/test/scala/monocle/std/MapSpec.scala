package monocle.std

import monocle.Lens
import monocle.TestUtil._
import monocle.std.map._
import org.specs2.scalaz.Spec
import scalaz.Equal

class MapSpec extends Spec {

  implicit val mapEq = Equal.equalA[Map[Int, String]]

  checkAll(Lens.laws(at[Int, String](1)))

}
