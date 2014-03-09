package monocle.std

import monocle.Prism
import monocle.TestUtil._
import monocle.std.boolean._
import org.specs2.scalaz.Spec


class BooleanSpec extends Spec {

  checkAll(Prism.laws(byteToBoolean))
  checkAll(Prism.laws(charToBoolean))
  checkAll(Prism.laws(intToBoolean))
  checkAll(Prism.laws(longToBoolean))

}
