package monocle.generic

import monocle.MonocleSuite
import shapeless.HNil

import scala.annotation.nowarn

class HListExample extends MonocleSuite {
  case class Example(i: Int, s: String, b: Boolean)

  test("toHList creates an Iso between a Generic (typically a case class) and HList") {
    assertEquals((Example(1, "bla", true) applyIso toHList get), (1 :: "bla" :: true :: HNil))

    //assertEquals( (Example(1, "bla", true) applyIso toHList applyLens first replace 5) ,  Example(5, "bla", true))
  }

  test("reverse creates an Iso between an HList and its reverse version") {
    assertEquals((1 :: "bla" :: true :: HNil applyIso reverse get), (true :: "bla" :: 1 :: HNil)): @nowarn
  }

  test("head creates a Lens from HList to the first element") {
    assertEquals((1 :: "bla" :: true :: HNil applyLens head get), 1): @nowarn
  }

  test("last creates a Lens from HList to the last element") {
    assertEquals((1 :: "bla" :: true :: HNil applyLens last get), true): @nowarn
  }

  test("tail creates a Lens from HList to its tail") {
    assertEquals((1 :: "bla" :: true :: HNil applyLens tail get), ("bla" :: true :: HNil)): @nowarn
  }

  test("init creates a Lens from HList to its init") {
    assertEquals((1 :: "bla" :: true :: HNil applyLens init get), (1 :: "bla" :: HNil)): @nowarn
  }
}
