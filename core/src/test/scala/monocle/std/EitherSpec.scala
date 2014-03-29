package monocle.std

import monocle.Prism
import monocle.std.either._
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._

class EitherSpec extends Spec {

  checkAll("std left" , Prism.laws(left[Int, String, Int]))
  checkAll("std right", Prism.laws(right[Int, String, String]))

}
