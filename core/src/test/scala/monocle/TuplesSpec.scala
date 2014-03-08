package monocle

import org.specs2.scalaz.Spec
import scalaz.Equal

class TuplesSpec extends Spec {
  import monocle.tuple._

  implicit val intStringEqual = Equal.equalA[(Int, String)]
  implicit val stringEqual = Equal.equalA[(String)]
  implicit val intEqual = Equal.equalA[(Int)]

  checkAll(Lens.laws(pairToSecondArg[Int, String]))
  checkAll(Lens.laws(pairToFirstArg[Int, String]))
}
