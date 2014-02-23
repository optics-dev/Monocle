package monocle

import monocle.TestHelper._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.specs2.scalaz._
import scalaz.std.AllInstances._


class LensSpec extends Spec {

  case class Example(s: String, i: Int)

  val StringLens = Macro.mkLens[Example, String]("s")

  implicit val exampleGen : Arbitrary[Example] =  Arbitrary(for {
    s <- arbitrary[String]
    i <- arbitrary[Int]
  } yield Example(s, i))

  implicit val exampleEq = defaultEqual[Example]

  checkAll(Lens.laws(StringLens))

}
