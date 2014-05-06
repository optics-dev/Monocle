package monocle

import org.scalacheck.{Properties, Arbitrary}
import scalaz.Equal
import org.scalacheck.Prop._


object IsoLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](iso: SimpleIso[S, A]) = new Properties("Iso") {
    import scalaz.syntax.equal._

    include(LensLaws(iso))
    include(PrismLaws(iso))

    property("double inverse") = forAll { (from: S, newValue: A) =>
      iso.reverse.reverse.get(from) === iso.get(from)
      iso.reverse.reverse.set(from, newValue) === iso.set(from, newValue)
    }

  }

}
