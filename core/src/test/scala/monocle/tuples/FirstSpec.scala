package monocle.tuples

import monocle.Lens
import monocle.tuples.Lenses._
import monocle.tuples.TuplesInstances._
import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._


class FirstSpec extends Spec {

  checkAll(Lens.laws(_1[(Int, String), Int]))
  checkAll(Lens.laws(_1[(Boolean, Int, String), Boolean]))

}
