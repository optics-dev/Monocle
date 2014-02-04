package monocle

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.specs2.scalaz._
import scalaz.Equal
import scalaz.std.AllInstances._

class MacroSpec extends Spec {

  case class Example(s: String, n: Int)

  implicit val exampleGen = Arbitrary(for {
    s <- arbitrary[String]
    n <- arbitrary[Int]
  } yield Example(s, n))

  implicit val exampleEq = new Equal[Example] {
    def equal(a1: Example, a2: Example): Boolean = a1 equals a2
  }

  val sLens = Macro.mkLens[Example, String]("s")
  val nLens = Macro.mkLens[Example, Int]("n")

  checkAll(Lens.laws(sLens))
  checkAll(Lens.laws(nLens))
}
