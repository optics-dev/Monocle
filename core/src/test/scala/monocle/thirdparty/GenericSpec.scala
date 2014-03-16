package monocle.thirdparty

import monocle.Lens
import monocle.TestUtil._
import monocle.thirdparty.generic._
import org.specs2.scalaz.Spec


class GenericSpec extends Spec {

  checkAll("_1 from Generic", Lens.laws(_1[Example, Int   , IntStringHList]))
  checkAll("_2 from Generic", Lens.laws(_2[Example, String, IntStringHList]))

}
