package monocle.law

import monocle.Prism
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.syntax.equal._
import scalaz.{Equal, Maybe}

object PrismLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](prism: Prism[S, A]) = new Properties("Prism") {
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
