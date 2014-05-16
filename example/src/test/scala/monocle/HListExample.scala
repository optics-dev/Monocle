package monocle

import monocle.syntax.iso._
import monocle.thirdparty.hlist._
import org.specs2.scalaz.Spec
import shapeless.{HNil, ::}
import monocle.syntax.lens._
import monocle.function.Fields._
import monocle.function.Reverse._


class HListExample extends Spec {

  case class Example(i : Int, s: String, b: Boolean)

  "_1 to _6 creates a Lens from HList to ith element" in {
    (1 :: "bla" :: true :: HNil |-> _1 get) shouldEqual 1
    (1 :: "bla" :: true :: HNil |-> _2 get) shouldEqual "bla"
    (1 :: "bla" :: true :: 5F :: 'c' :: 7L ::  HNil |-> _6 get) shouldEqual 7L

    (1 :: "bla" :: true :: HNil  |-> _1 modify(_ + 1)) shouldEqual 2 :: "bla" :: true :: HNil
  }

  "toHList creates an Iso between a Generic (typically case class) and HList" in {
    (Example(1, "bla", true) <-> toHList get) shouldEqual (1 :: "bla" :: true :: HNil)

    (Example(1, "bla", true) <-> toHList |-> _1 set 5) shouldEqual Example(5, "bla", true)
  }

  "reverse creates an Iso between an HList and its reverse version" in {
    (1 :: "bla" :: true :: HNil <-> reverse get) shouldEqual (true :: "bla" :: 1 :: HNil)
  }

}
