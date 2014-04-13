package monocle.std

import monocle.{Lens}
import org.specs2.scalaz.Spec

import monocle.std.list._
import scalaz.Equal

class ListSpec extends Spec {

  implicit val optionEq = Equal.equalA[Option[Int]]
  implicit val listEq = Equal.equalA[List[Int]]

  checkAll("last", Lens.laws(last[Int]))
}
