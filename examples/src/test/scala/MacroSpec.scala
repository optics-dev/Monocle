import lens.Macro
import org.scalacheck.Arbitrary
import org.scalatest.Matchers._
import org.scalatest.PropSpec
import org.scalatest.prop.PropertyChecks
import scala.language.higherKinds
import org.scalacheck.Arbitrary._

class MacroSpec extends PropSpec with PropertyChecks {

  case class Example(s: String, n: Int)

  implicit val exampleGen = Arbitrary(for {
    s <- arbitrary[String]
    n <- arbitrary[Int]
  } yield Example(s, n))

  property("generate getter") {
    val sGetter = Macro.mkGetter[Example, String]("s")
    val nGetter = Macro.mkGetter[Example, Int]("n")

    forAll { (example: Example) =>
      sGetter(example) should be (example.s)
      nGetter(example) should be (example.n)
    }
  }

  property("generate setter") {
    val sSetter = Macro.mkSetter[Example, String]("s")
    val nSetter = Macro.mkSetter[Example, Int]("n")

    forAll { (example: Example, newS: String, newN: Int) =>
      sSetter(example, newS) should be (example.copy(s = newS))
      nSetter(example, newN) should be (example.copy(n = newN))
    }
  }

  property("generate lens") {
    val sLens = Macro.mkLens[Example, String]("s")
    val nLens = Macro.mkLens[Example, Int]("n")

    forAll { (example: Example, newS: String, newN: Int) =>
      sLens.get(example) should be (example.s)
      nLens.get(example) should be (example.n)

      sLens.set(example, newS) should be (example.copy(s = newS))
      nLens.set(example, newN) should be (example.copy(n = newN))
    }
  }

}
