package monocle

import monocle.TestUtil._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.specs2.scalaz._
import scalaz.Equal

class LensSpec extends Spec {

  case class Example(s: String, i: Int)

  val StringLens = SimpleLens[Example, String](_.s, (ex, s) => ex.copy(s = s))

  implicit val exampleGen: Arbitrary[Example] = Arbitrary(for {
    s <- arbitrary[String]
    i <- arbitrary[Int]
  } yield Example(s, i))

  implicit val exampleEq = Equal.equalA[Example]

  checkAll(LensLaws(StringLens))

}
