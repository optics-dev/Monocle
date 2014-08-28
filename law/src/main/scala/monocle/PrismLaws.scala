package monocle

import scalaz.{Maybe, Equal}
import scalaz.syntax.equal._
import org.scalacheck.Prop._
import org.scalacheck.{Properties, Arbitrary}

object PrismLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](prism: SimplePrism[S, A]) = new Properties("Prism") {
    include(TraversalLaws(prism.asTraversal))

    property("reverseGet - getOption") = forAll { value: A =>
      prism.getMaybe(prism.reverseGet(value)) === Maybe.just(value)
    }

    property("getOption - reverseGet") = forAll { from: S =>
    // if we can extract an A from S, then this A fully describes S
      prism.getMaybe(from).map { maybeA =>
        prism.reverseGet(maybeA) === from
      } getOrElse true
    }
  }

}
