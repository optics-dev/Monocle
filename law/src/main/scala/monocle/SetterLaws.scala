package monocle

import org.scalacheck.{Properties, Arbitrary}
import scalaz.Equal
import org.scalacheck.Prop._

object SetterLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary](setter: SimpleSetter[S, A]) = new Properties("Setter") {
    import scalaz.syntax.equal._

    property("modify - identity") = forAll { from: S =>
      setter.modify(from, identity) === from
    }

    property("set - set") = forAll { (from: S, newValue: A) =>
      val setOnce = setter.set(from, newValue)
      setOnce === setter.set(setOnce, newValue)
    }
  }

}
