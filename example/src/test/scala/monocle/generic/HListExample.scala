package monocle.generic

import monocle.MonocleSuite
import monocle.function.all._
import monocle.generic.all._
import monocle.syntax.all._
import shapeless.HNil

class HListExample extends MonocleSuite {

  case class Example(i : Int, s: String, b: Boolean)

  test("_1 to _6 creates a Lens from HList to ith element") {
    (1 :: "bla" :: true :: HNil applyLens first get) shouldEqual 1
    (1 :: "bla" :: true :: HNil applyLens second get) shouldEqual "bla"
    (1 :: "bla" :: true :: 5F :: 'c' :: 7L ::  HNil applyLens sixth get) shouldEqual 7L

    (1 :: "bla" :: true :: HNil  applyLens first modify(_ + 1)) shouldEqual 2 :: "bla" :: true :: HNil
  }

  test("toHList creates an Iso between a Generic (typically a case class) and HList") {
    (Example(1, "bla", true) applyIso toHList get) shouldEqual (1 :: "bla" :: true :: HNil)

//    (Example(1, "bla", true) applyIso toHList applyLens first set 5) shouldEqual Example(5, "bla", true)
  }

  test("reverse creates an Iso between an HList and its reverse version") {
    (1 :: "bla" :: true :: HNil applyIso reverse get) shouldEqual (true :: "bla" :: 1 :: HNil)
  }

  test("head creates a Lens from HList to the first element") {
    (1 :: "bla" :: true :: HNil applyLens head get) shouldEqual 1
  }

  test("last creates a Lens from HList to the last element") {
    (1 :: "bla" :: true :: HNil applyLens last get) shouldEqual true
  }

  test("tail creates a Lens from HList to its tail") {
    (1 :: "bla" :: true :: HNil applyLens tail get) shouldEqual ("bla" :: true :: HNil)
  }

  test("init creates a Lens from HList to its init") {
    (1 :: "bla" :: true :: HNil applyLens init get) shouldEqual (1 :: "bla" :: HNil)
  }

}
