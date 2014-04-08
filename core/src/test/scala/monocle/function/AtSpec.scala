package monocle.function

import monocle.TestUtil._
import monocle.Traversal
import monocle.function.At._
import org.specs2.scalaz.Spec


class AtSpec extends Spec {

  checkAll("at Map", Traversal.laws(at[Map[Int, String], Int, String](2)))

}
