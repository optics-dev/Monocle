package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function.{Cons1Tests, EachTests, ReverseTests, Snoc1Tests}

import scala.annotation.nowarn

class Tuple2Spec extends MonocleSuite {
  checkAll("each tuple2", EachTests[(Int, Int), Int])
  checkAll("reverse tuple2", ReverseTests[(Int, Char), (Char, Int)]): @nowarn
  checkAll("cons1 tuple2", Cons1Tests[(Int, Char), Int, Char])
  checkAll("snoc1 tuple2", Snoc1Tests[(Int, Char), Int, Char])
}
