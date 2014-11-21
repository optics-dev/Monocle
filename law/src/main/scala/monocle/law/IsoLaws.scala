package monocle.law

import monocle.Iso
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.Equal
import scalaz.Id._
import scalaz.syntax.equal._


object IsoLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](iso: Iso[S, A]) = new Properties("Iso") {

    property("get and reverseGet forms an Isomorphism") = forAll { (s: S, a: A) =>
      (iso.reverseGet compose iso.get)(s)        === s
      (iso.get        compose iso.reverseGet)(a) === a
    }

    property("set is a weaker version of reverseGet") = forAll { (s: S, a: A) =>
      iso.set(a)(s) === iso.reverseGet(a)
    }

    property("modifyF with Id does not do anything") = forAll { s: S =>
      iso.modifyF[Id](id.point[A](_))(s) === s
    }

    property("modify with id does not do anything") = forAll { s: S =>
      iso.modify(identity)(s) === s
    }

  }

}
