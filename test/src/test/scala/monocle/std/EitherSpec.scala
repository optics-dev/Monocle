package monocle.std

import monocle.TestUtil._
import monocle.law.PrismLaws
import org.specs2.scalaz.Spec

class EitherSpec extends Spec {

  checkAll("std left" , PrismLaws(stdLeft[Int, String]))
  checkAll("std right", PrismLaws(stdRight[Int, String]))

}
