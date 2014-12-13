package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.OptionalLaws
import org.specs2.scalaz.Spec

class BooleanSpec extends Spec {

  checkAll("Boolean index bit", OptionalLaws(index[Boolean, Int, Boolean](0)))

}
