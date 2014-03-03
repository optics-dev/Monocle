package monocle.std

import monocle.Prism
import monocle.std.option._
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._

class OptionSpec extends Spec {

  checkAll(Prism.laws(_Some[Int, Int]))
  checkAll(Prism.laws(_None[Long]))

}
