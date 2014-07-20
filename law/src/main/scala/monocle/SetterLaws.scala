package monocle

import  _root_.scalaz.Equal
import  _root_.scalaz.syntax.equal._
import org.scalacheck.Prop._
import org.scalacheck.{Properties, Arbitrary}

object SetterLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary](setter: SimpleSetter[S, A]) = new Properties("Setter") {
    property("modify - identity") = forAll { from: S =>
      setter.modify(from, identity) === from
    }

    property("set - set") = forAll { (from: S, newValue: A) =>
      val setOnce = setter.set(from, newValue)
      setOnce === setter.set(setOnce, newValue)
    }
  }

}
