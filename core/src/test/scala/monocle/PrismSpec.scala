package monocle

import monocle.std.int._
import org.specs2.scalaz.Spec
import scalaz.Equal

class PrismSpec extends Spec {

  implicit val intEqual  = Equal.equalA[Int]
  implicit val charEqual = Equal.equalA[Char]

  checkAll(Prism.laws(intToChar))

}
