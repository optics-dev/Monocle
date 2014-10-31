package monocle.law

import monocle.Setter
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.Equal
import scalaz.syntax.equal._

object SetterLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary](setter: Setter[S, A]) = new Properties("Setter") {
    property("modify . id  == id") = forAll { s: S =>
      setter.modify(identity)(s) === s
    }

    property("set . set == set") = forAll { (s: S, a: A) =>
      (setter.set(a) compose setter.set(a))(s) === setter.set(a)(s)
    }
  }

}