package monocle

import scalaz.Equal
import org.scalacheck.Prop._
import org.scalacheck.{Properties, Arbitrary}


object OptionalLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](optional: SimpleOptional[S, A]) = new Properties("Optional") {
    include(TraversalLaws(optional))

    property("getAll size <= 1") = forAll { from: S =>
      optional.getAll(from).size <= 1
    }

  }

}
