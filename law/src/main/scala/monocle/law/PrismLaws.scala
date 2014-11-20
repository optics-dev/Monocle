package monocle.law

import monocle.Prism
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.Id._
import scalaz.Maybe.Just
import scalaz.syntax.equal._
import scalaz.{Equal, Maybe}

object PrismLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](prism: Prism[S, A]) = new Properties("Prism") {

    property("reverseGet produces a value") = forAll { a: A =>
      prism.getMaybe(prism.reverseGet(a)) === Maybe.just(a)
    }

    property("if a Prism match you can always go back to the source") = forAll { s: S =>
      prism.getOrModify(s).fold(identity, prism.reverseGet) === s
    }

    /** modifyF does not change the number of targets */
    property("modifyF with Id does not do anything") = forAll { s: S =>
      prism.modifyF[Id](id.point[A](_))(s) === s
    }

    /** modify does not change the number of targets */
    property("modify with id does not do anything") = forAll { s: S =>
      prism.modify(identity)(s) === s
    }

    property("setMaybe only succeeds when the prism is matching") = forAll { (s: S, a: A) =>
      prism.setMaybe(a)(s) === prism.getMaybe(s).map(_ => prism.set(a)(s))
    }

    property("modifyMaybe with id is isomorphomic to isMatching") = forAll { s: S =>
      prism.modifyMaybe(identity)(s) === prism.getMaybe(s).map(_ => s)
    }

  }

}
