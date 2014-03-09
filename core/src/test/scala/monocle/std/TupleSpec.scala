package monocle.std

import monocle.Lens
import monocle.TestUtil._
import monocle.std.tuple._
import org.specs2.scalaz.Spec
import scalaz.Equal

class TupleSpec extends Spec {

  implicit val intStringEqual = Equal.equalA[(Int, String)]

  checkAll("_1", Lens.laws(_1[Int, String, Int]))
  checkAll("_2", Lens.laws(_2[Int, String, String]))
}
