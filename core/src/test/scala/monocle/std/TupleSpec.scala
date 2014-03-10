package monocle.std

import monocle.TestUtil._
import monocle.std.tuple._
import monocle.{ Traversal, Lens }
import org.specs2.scalaz.Spec

class TupleSpec extends Spec {

  checkAll("_1", Lens.laws(_1[Int, String, Int]))
  checkAll("_2", Lens.laws(_2[Int, String, String]))

  checkAll("both", Traversal.laws(both[Int, Int]))
}
