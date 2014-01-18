package lens

import lens.impl.HLens
import lens.util.Identity
import org.scalatest.Matchers._
import org.scalatest.PropSpec
import org.scalatest.prop.PropertyChecks
import scala.language.higherKinds
import scalaz.Functor


class LensSpec extends PropSpec with PropertyChecks  {


  case class Example(s: String)

  object StringLens extends HLens[Example, String] {
    protected def lensFunction[F[_] : Functor](lift: String => F[String], example: Example): F[Example] = {
      implicitly[Functor[F]].map(lift(example.s))(newValue => example.copy(s = newValue))
    }
  }

  val example = Example("blabla")

  property("set - get") {
    forAll { (s: String) =>
      StringLens.get(StringLens.set(example, s)) should be (s)
    }
  }

  property("get - set") {
    forAll { (s: String) =>
      StringLens.set(example, StringLens.get(example)) should be (example)
    }
  }

  property("set - set") {
    forAll { (s: String) =>
      StringLens.set(example, s) should be (StringLens.set(StringLens.set(example, s), s))
    }
  }

  property("modify - id") {
    forAll { (s: String) =>
      StringLens.modify(example, identity) should be (example)
    }
  }

  property("lift - id") {
    forAll { (s: String) =>
      StringLens.lift(example, Identity[String] ).value should be (example)
    }
  }

}
