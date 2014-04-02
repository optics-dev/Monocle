package monocle.std

import monocle.{Traversal, Prism}
import monocle.std.option._
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._

class OptionSpec extends Spec {

  checkAll("some", Prism.laws(some[Int, Int]))
  checkAll("none", Prism.laws(none[Long]))

  checkAll("each option", Traversal.laws(Traversal.each[Option[Int], Int]))

}
