package monocle

import monocle.syntax.iso._
import monocle.thirdparty.hlist._
import org.specs2.scalaz.Spec
import shapeless.HNil


class HListExample extends Spec {

  case class Example(i : Int, s: String, b: Boolean)

  val example = Example(1, "bla", true)
  val hlist   = example <-> toHList get

  "toHList creates an Iso between a Generic (typically case class) and HList" in {
    (example <-> toHList get) shouldEqual (1 :: "bla" :: true :: HNil)
  }

  "_1 creates a Lens toward the first element of a non-empty HList" in {
    _1.get(hlist) shouldEqual 1

    _1.set(hlist, 2)     shouldEqual (2     :: "bla" :: true :: HNil)
    _1.set(hlist, false) shouldEqual (false :: "bla" :: true :: HNil)
  }

  "_2 creates a Lens toward the second element of an Hlist with 2 or more elements" in {
    _2.get(hlist) shouldEqual "bla"
  }

}
