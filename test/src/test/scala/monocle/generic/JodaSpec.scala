package monocle.generic

import monocle.MonocleSuite
import monocle.date.joda
import monocle.law.discipline.LensTests
import eu.timepit.refined.scalacheck.numericArbitrary._

class JodaSpec extends MonocleSuite {

  checkAll("minute", LensTests(joda.minute))
  checkAll("hour"  , LensTests(joda.hour))
}
