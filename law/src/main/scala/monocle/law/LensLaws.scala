package monocle.law

import monocle.SimpleLens
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.Equal
import scalaz.syntax.equal._

object LensLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](lens: SimpleLens[S, A]) = new Properties("Lens") {
    include(TraversalLaws(lens.asTraversal))

    property("set - get") = forAll { (s: S, a: A) =>
      lens.get(lens.set(a)(s)) === a
    }

    property("get - set") = forAll { s: S =>
      lens.set(lens.get(s))(s) === s
    }
  }

}
