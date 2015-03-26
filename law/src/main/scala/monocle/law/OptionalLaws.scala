package monocle.law

import monocle.Optional
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.Equal
import scalaz.Id._
import scalaz.std.option._
import scalaz.syntax.equal._

object OptionalLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](optional: Optional[S, A]) = new Properties("Optional") {

    property("setting what you get does not do anything") = forAll { s: S =>
      optional.getOrModify(s).fold(identity, optional.set(_)(s)) === s
    }

    property("you get what you set") = forAll { (s: S, a: A) =>
      optional.getOption(optional.set(a)(s)) === optional.getOption(s).map(_ => a)
    }

    /** calling set twice is the same as calling set once */
    property("set is idempotent") = forAll { (s: S, a: A) =>
      optional.set(a)(optional.set(a)(s)) === optional.set(a)(s)
    }

    /** modifyF does not change the number of targets */
    property("modifyF with Id does not do anything") = forAll { s: S =>
      optional.modifyF[Id](id.point[A](_))(s) === s
    }

    /** modify does not change the number of targets */
    property("modify with id does not do anything") = forAll { s: S =>
      optional.modify(identity)(s) === s
    }

    property("setOption only succeeds when the Optional is matching") = forAll { (s: S, a: A) =>
      optional.setOption(a)(s) === optional.getOption(s).map(_ => optional.set(a)(s))
    }

    property("modifyOption with id is isomorphomic to isMatching") = forAll { s: S =>
      optional.modifyOption(identity)(s) === optional.getOption(s).map(_ => s)
    }

  }

}
