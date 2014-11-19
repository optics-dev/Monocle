package monocle.law

import monocle.Setter
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.Equal
import scalaz.syntax.equal._

object SetterLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary](setter: Setter[S, A]) = new Properties("Setter") {

    /** calling set twice is the same as calling set once */
    property("set is idempotent") = forAll { (s: S, a: A) =>
      (setter.set(a) compose setter.set(a))(s) === setter.set(a)(s)
    }

    /** modify does not change the number of targets */
    property("modify preserves the structure") = forAll { s: S =>
      setter.modify(identity)(s) === s
    }

  }

}