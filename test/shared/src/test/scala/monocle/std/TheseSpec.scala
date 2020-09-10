package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.OptionalTests
import monocle.law.discipline.PrismTests

class TheseSpec extends MonocleSuite {
  import cats.laws.discipline.arbitrary._

  checkAll("These - Disjunction", PrismTests(theseToDisjunction[Int, String]))

  checkAll("These - Left", OptionalTests(theseLeft[Int, String]))

  checkAll("These - Right", OptionalTests(theseRight[Int, String]))
}
