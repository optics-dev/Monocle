package monocle.thirdparty

import monocle.TestUtil._
import monocle.thirdparty.hlist._
import monocle.{Iso, Lens}
import org.scalacheck.Arbitrary._
import org.specs2.scalaz.Spec
import shapeless.HNil

class HListSpec extends Spec {

  checkAll("_1 from HList", Lens.laws(_1[Int, HL[String, HNil], Int]))
  checkAll("_2 from HList", Lens.laws(_2[Int, String, HNil, String]))

  checkAll("toHList", Iso.laws(toHList[Example, IntStringHList]) )

}
