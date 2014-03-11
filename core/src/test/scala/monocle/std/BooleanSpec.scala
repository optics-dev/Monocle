package monocle.std

import monocle.Prism
import monocle.TestUtil._
import monocle.std.boolean._
import org.specs2.scalaz.Spec

class BooleanSpec extends Spec {

  checkAll("byteToBoolean", Prism.laws(byteToBoolean))
  checkAll("charToBoolean", Prism.laws(charToBoolean))
  checkAll("intToBoolean" , Prism.laws(intToBoolean ))
  checkAll("longToBoolean", Prism.laws(longToBoolean))

}
