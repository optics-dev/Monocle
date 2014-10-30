package monocle.law

import monocle.SimpleOptional
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.Equal


object OptionalLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](optional: SimpleOptional[S, A]) = new Properties("Optional") {
    include(TraversalLaws(optional.asTraversal))

    property("getAll size <= 1") = forAll { from: S =>
      optional.asTraversal.getAll(from).length <= 1
    }

  }

}
