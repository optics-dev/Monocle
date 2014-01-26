package lens

import lens.impl.HLens
import lens.util.Identity
import org.scalatest.Matchers._
import org.scalatest.PropSpec
import org.scalatest.prop.PropertyChecks
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._


class LensSpec extends PropSpec with PropertyChecks  {

  case class Example(s: String, i: Int)

  val StringLens = HLens[Example, String](_.s, (a, b) => a.copy(s = b))

  implicit val exampleGen : Arbitrary[Example] =  Arbitrary(for {
    s <- arbitrary[String]
    i <- arbitrary[Int]
  } yield Example(s, i))

  property("set - get") {
    forAll { (example: Example, s: String) =>
      StringLens.get(StringLens.set(example, s)) should be (s)
    }
  }

  property("get - set") {
    forAll { (example: Example, s: String) =>
      StringLens.set(example, StringLens.get(example)) should be (example)
    }
  }

  property("set - set") {
    forAll { (example: Example, s: String) =>
      StringLens.set(example, s) should be (StringLens.set(StringLens.set(example, s), s))
    }
  }

  property("modify - id") {
    forAll { (example: Example) =>
      StringLens.modify(example, identity) should be (example)
    }
  }

  property("lift - id") {
    forAll { (example: Example) =>
      StringLens.lift(example, Identity[String] ).value should be (example)
    }
  }

}
