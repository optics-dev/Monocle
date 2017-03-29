package monocle.function

import monocle.MonocleSuite

import scalaz.Tags
import scalaz.std.anyVal._

class WrappedExample extends MonocleSuite {

  test("wrapped is an Iso") {
    (Tags.Max(100) applyIso wrapped get) shouldEqual 100
  }

  test("unwrapped is an Iso") {
    ("Hello" applyIso unwrapped get) shouldEqual Tags.Dual("Hello")
  }

}
