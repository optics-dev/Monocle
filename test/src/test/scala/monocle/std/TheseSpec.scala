package monocle.std

import monocle.TestUtil._
import monocle.law.PrismLaws
import org.specs2.scalaz.Spec

class TheseSpec extends Spec {
  checkAll("These - Disjunction", PrismLaws(theseToDisjunction[Int, String]))
  checkAll("These - This"       , PrismLaws(theseToThis[Int, String]))
  checkAll("These - That"       , PrismLaws(theseToThat[Int, String]))
  checkAll("These - Both"       , PrismLaws(theseToBoth[Int, String]))
}
