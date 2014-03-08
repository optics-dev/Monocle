package monocle.std

import monocle.Lens
import monocle.std.tuple._
import org.specs2.scalaz.Spec
import scalaz.Equal

class TupleSpec extends Spec {

  implicit val intStringEqual = Equal.equalA[(Int, String)]
  implicit val stringEqual = Equal.equalA[(String)]
  implicit val intEqual = Equal.equalA[(Int)]

  checkAll(Lens.laws(_1[Int, String]))
  checkAll(Lens.laws(_2[Int, String]))
}
