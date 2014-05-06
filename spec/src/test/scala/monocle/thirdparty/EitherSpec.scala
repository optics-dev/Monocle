package monocle.thirdparty

import monocle.{PrismLaws, Prism}
import monocle.thirdparty.either._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.specs2.scalaz.Spec
import scalaz.\/
import scalaz.\/.fromEither
import scalaz.std.AllInstances._

class EitherSpec extends Spec {

  implicit def arbitraryEither[A: Arbitrary, B: Arbitrary]: Arbitrary[A \/ B] =
    Arbitrary(arbitrary[Either[A, B]] map fromEither)

  checkAll("scalaz left" , PrismLaws( left[Int, String, Int]))
  checkAll("scalaz right", PrismLaws(right[Int, String, String]))

}
