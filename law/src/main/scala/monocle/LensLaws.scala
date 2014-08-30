package monocle

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.syntax.equal._
import scalaz.{Equal, Reader}

object LensLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](lens: SimpleLens[S, A]) = new Properties("Lens") {
    include(TraversalLaws(lens.asTraversal))

    property("modifyK . id == id") = forAll { s: S =>
      lens.modifyK(Reader.apply(identity)).run(s) === s
    }

    property("set - get") = forAll { (s: S, a: A) =>
      lens.get(lens.set(a)(s)) === a
    }

    property("get - set") = forAll { s: S =>
      lens.set(lens.get(s))(s) === s
    }
  }

}
