package monocle.focus

import monocle.MonocleSuite
import monocle.law.discipline.LensTests
import monocle.law.discipline.IsoTests
import monocle.Focus
import org.scalacheck.Arbitrary
import cats.Eq
import cats.syntax.all.*

final class NamedTupleLawTest extends MonocleSuite {

  given namedTupleArbitrary[Names <: Tuple, Values <: Tuple](using
    Values: Arbitrary[Values]
  ): Arbitrary[NamedTuple.NamedTuple[Names, Values]] =
    Arbitrary(Values.arbitrary.map(identity))

  given namedTupleEq[Names <: Tuple, Values <: Tuple](using
    Values: Eq[Values]
  ): Eq[NamedTuple.NamedTuple[Names, Values]] =
    Values.contramap(_.toTuple)

  checkAll("Focus named tuple field", LensTests(Focus[(a: Int, b: String)](_.a)))
  checkAll("Focus single-field named tuple", IsoTests(Focus[(a: Int)](_.a)))
}
