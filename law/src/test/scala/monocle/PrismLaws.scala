package monocle

import org.scalacheck.{Properties, Arbitrary}
import org.scalacheck.Prop._
import scalaz.Equal


object PrismLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](prism: SimplePrism[S, A]) = new Properties("Prism") {
    import scalaz.syntax.equal._
    import scalaz.std.option._

    include(TraversalLaws(prism))

    property("reverseGet - getOption") = forAll { value: A =>
      prism.getOption(prism.reverseGet(value)) === Some(value)
    }

    property("getOption - reverseGet") = forAll { (from: S, newValue: A) =>
    // if we can extract an A from S, then this A fully describes S
      prism.getOption(from).map { someA =>
        prism.reverseGet(someA) === from
      } getOrElse true
    }
  }

}
