package monocle.law

import monocle.Lens
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.Equal
import scalaz.Id._
import scalaz.syntax.equal._

object LensLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](lens: Lens[S, A]) = new Properties("Lens") {

    property("setting what you get does not do anything") = forAll { s: S =>
      lens.set(lens.get(s))(s) === s
    }
    
    property("you get what you set") = forAll { (s: S, a: A) =>
      lens.get(lens.set(a)(s)) === a
    }

    /** calling set twice is the same as calling set once */
    property("set is idempotent") = forAll { (s: S, a: A) =>
      lens.set(a)(lens.set(a)(s)) === lens.set(a)(s)
    }

    property("modifyF with Id does not do anything") = forAll { s: S =>
      lens.modifyF[Id](id.point[A](_))(s) === s
    }

    property("modify with id does not do anything") = forAll { s: S =>
      lens.modify(identity)(s) === s
    }

  }

}
