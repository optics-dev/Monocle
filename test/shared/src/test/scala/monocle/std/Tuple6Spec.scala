package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function.{Cons1Tests, EachTests, Snoc1Tests}

class Tuple6Spec extends MonocleSuite {
  checkAll("each tuple6", EachTests[(Int, Int, Int, Int, Int, Int), Int])
  checkAll(
    "cons1 tuple6",
    Cons1Tests[(Int, Char, Boolean, String, Long, Float), Int, (Char, Boolean, String, Long, Float)]
  )
  checkAll(
    "snoc1 tuple6",
    Snoc1Tests[(Int, Char, Boolean, String, Long, Float), (Int, Char, Boolean, String, Long), Float]
  )
}
