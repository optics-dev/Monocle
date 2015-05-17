package monocle.std

import monocle.MonocleSuite
import monocle.function._
import monocle.law.discipline.LensTests
import monocle.law.discipline.function.{Cons1Tests, EachTests, ReverseTests, Snoc1Tests}

class Tuple2Spec extends MonocleSuite {

  checkAll("first tuple2", LensTests(first[(Int, Char), Int]))
  checkAll("second tuple2", LensTests(second[(Int, Char), Char]))

  checkAll("each tuple2", EachTests[(Int, Int), Int])
  checkAll("reverse tuple2", ReverseTests[(Int, Char), (Char, Int)])
  checkAll("cons1 tuple2", Cons1Tests[(Int, Char), Int, Char])
  checkAll("snoc1 tuple2", Snoc1Tests[(Int, Char), Int, Char])

}
