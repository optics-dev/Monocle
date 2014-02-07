package monocle

import monocle.TestHelper._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.specs2.scalaz._
import scalaz.std.AllInstances._


class LensSpec extends Spec {

  case class Example(s: String, i: Int)

  val StringLens = SimpleLens[Example, String](_.s, (a, b) => a.copy(s = b))

  implicit val exampleGen : Arbitrary[Example] =  Arbitrary(for {
    s <- arbitrary[String]
    i <- arbitrary[Int]
  } yield Example(s, i))

  implicit val exampleEq = defaultEqual[Example]

  checkAll(SimpleLens.laws(StringLens))

}
