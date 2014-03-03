package monocle.thirdparty

import monocle.Prism
import monocle.thirdparty.scalazEither._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.specs2.scalaz.Spec
import scalaz.\/
import scalaz.\/._
import scalaz.std.AllInstances._

class ScalazEitherSpec extends Spec {

  implicit def arbitraryEither[A: Arbitrary, B: Arbitrary]: Arbitrary[A \/ B] =
    Arbitrary(arbitrary[Either[A, B]] map fromEither)

  checkAll(Prism.laws(_Left[Int, String, Int]))
  checkAll(Prism.laws(_Right[Int, String, String]))

}
