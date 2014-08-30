package monocle

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.Equal
import scalaz.syntax.equal._


object IsoLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](iso: SimpleIso[S, A]) = new Properties("Iso") {

    include(LensLaws(iso.asLens))

    property("reverse . reverse == id") = forAll { (s: S, a: A) =>
      iso.reverse.reverse.get(s)    === iso.get(s)
      iso.reverse.reverse.set(a)(s) === iso.set(a)(s)
    }
  }

}
