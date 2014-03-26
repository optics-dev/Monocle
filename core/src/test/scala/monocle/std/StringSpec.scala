//Created By Ilan Godik
package monocle.std

import monocle.TestUtil._
import monocle.std.string._
import org.specs2.scalaz.Spec
import monocle.Prism

class StringSpec extends Spec {

  checkAll("stringToBoolean", Prism.laws(stringToBoolean))
  checkAll("stringToByte", Prism.laws(stringToByte))
  checkAll("stringToShort", Prism.laws(stringToShort))
  checkAll("stringToInt", Prism.laws(stringToInt))
  checkAll("stringToLong", Prism.laws(stringToLong))

  //Floating point comparisons?
  //checkAll("stringToFloat", Prism.laws(stringToFloat))
  //checkAll("stringToDouble", Prism.laws(stringToDouble))

}
