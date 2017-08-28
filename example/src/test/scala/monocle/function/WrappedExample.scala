package monocle.function

import monocle.MonocleSuite

import newts.{Dual, Max}

class WrappedExample extends MonocleSuite {

  test("wrapped is an Iso") {
    (Max(100) applyIso wrapped get) shouldEqual 100
  }

  test("unwrapped is an Iso") {
    ("Hello" applyIso unwrapped[Dual[String]] get) shouldEqual Dual("Hello")
  }

}
