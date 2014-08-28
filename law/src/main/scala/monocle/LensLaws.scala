package monocle

import scalaz.Equal
import scalaz.Id._
import scalaz.syntax.equal._
import org.scalacheck.Prop._
import org.scalacheck.{Properties, Arbitrary}

object LensLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](lens: SimpleLens[S, A]) = new Properties("Lens") {
    include(TraversalLaws(lens.asTraversal))

    property("lift - identity") = forAll { from: S =>
      lens.lift[Id](from, id.point[A](_)) === from
    }

    property("set - get") = forAll { (from: S, newValue: A) =>
      lens.get(lens.set(from, newValue)) === newValue
    }

    property("get - set") = forAll { from: S =>
      lens.set(from, lens.get(from)) === from
    }
  }

}
