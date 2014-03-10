package monocle.thirdparty

import monocle.TestUtil._
import monocle.thirdparty.hlist._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.specs2.scalaz.Spec
import scalaz.Equal
import shapeless.HNil

class HListSpec extends Spec {

  // todo : generalise Arbitrary and Equal
  implicit val arbitraryHListIntString: Arbitrary[HL[Int, HL[String, HNil]]] = Arbitrary(for {
    i <- arbitrary[Int]
    s <- arbitrary[String]
  } yield i :: s :: HNil)

  implicit val equalHListIntString = Equal.equalA[HL[Int, HL[String, HNil]]]

  checkAll("_1 from HList", Lens.laws(_1[Int, HL[String, HNil], Int]))
  checkAll("_2 from HList", Lens.laws(_2[Int, String, HNil, String]))

}
