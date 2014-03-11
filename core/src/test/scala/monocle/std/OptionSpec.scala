package monocle.std

import monocle.Prism
import monocle.std.option._
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._

class OptionSpec extends Spec {

  checkAll("_Some", Prism.laws(_Some[Int, Int]))
  checkAll("_None", Prism.laws(_None[Long    ]))

}
