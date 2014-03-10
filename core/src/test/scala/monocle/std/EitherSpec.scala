package monocle.std

import monocle.Prism
import monocle.std.either._
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._

class EitherSpec extends Spec {

  checkAll("_Left", Prism.laws(_Left[Int, String, Int]))
  checkAll("_Right", Prism.laws(_Right[Int, String, String]))

}
