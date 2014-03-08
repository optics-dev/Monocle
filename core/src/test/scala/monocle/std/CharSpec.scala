package monocle.std

import org.specs2.scalaz.Spec
import monocle.Prism
import monocle.std.char._
import scalaz.Equal

class CharSpec extends Spec {

  implicit val intEq = Equal.equalA[Int]
  implicit val longEq = Equal.equalA[Long]
  implicit val charEq = Equal.equalA[Char]

  checkAll(Prism.laws(intToChar))
  checkAll(Prism.laws(longToChar))

}
