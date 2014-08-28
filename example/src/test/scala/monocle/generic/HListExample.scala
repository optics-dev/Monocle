package monocle.generic

import monocle.function._
import monocle.syntax._
import org.specs2.scalaz.Spec
import shapeless.HNil

class HListExample extends Spec {

  case class Example(i : Int, s: String, b: Boolean)

  "_1 to _6 creates a Lens from HList to ith element" in {
    (1 :: "bla" :: true :: HNil applyLens first get) ==== 1
    (1 :: "bla" :: true :: HNil applyLens second get) ==== "bla"
    (1 :: "bla" :: true :: 5F :: 'c' :: 7L ::  HNil applyLens sixth get) ==== 7L

    (1 :: "bla" :: true :: HNil  applyLens first modify(_ + 1)) ==== 2 :: "bla" :: true :: HNil
  }

  "toHList creates an Iso between a Generic (typically a case class) and HList" in {
    (Example(1, "bla", true) applyIso toHList get) ==== (1 :: "bla" :: true :: HNil)

//    (Example(1, "bla", true) applyIso toHList applyLens first set 5) ==== Example(5, "bla", true)
  }

  "reverse creates an Iso between an HList and its reverse version" in {
    (1 :: "bla" :: true :: HNil applyIso reverse get) ==== (true :: "bla" :: 1 :: HNil)
  }

  "head creates a Lens from HList to the first element" in {
    (1 :: "bla" :: true :: HNil applyLens head get) ==== 1
  }

  "last creates a Lens from HList to the last element" in {
    (1 :: "bla" :: true :: HNil applyLens last get) ==== true
  }

  "tail creates a Lens from HList to its tail" in {
    (1 :: "bla" :: true :: HNil applyLens tail get) ==== ("bla" :: true :: HNil)
  }

  "init creates a Lens from HList to its init" in {
    (1 :: "bla" :: true :: HNil applyLens init get) ==== (1 :: "bla" :: HNil)
  }

}
