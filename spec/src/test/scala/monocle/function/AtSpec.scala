package monocle.function

import monocle.TestUtil._
import monocle.TraversalLaws
import monocle.function.At._
import org.specs2.scalaz.Spec

class AtSpec extends Spec {

  checkAll("at Map", TraversalLaws(at[Map[Int, String], Int, String](2)))

}
