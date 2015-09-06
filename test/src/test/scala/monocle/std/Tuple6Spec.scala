package monocle.std

import monocle.MonocleSuite
import monocle.function.all._
import monocle.law.discipline.LensTests
import monocle.law.discipline.function.{Cons1Tests, EachTests, Snoc1Tests}

class Tuple6Spec extends MonocleSuite {

  checkAll("each tuple6" , EachTests[(Int, Int, Int, Int, Int, Int), Int])

  checkAll("first tuple6" , LensTests(first[(Int, Char, Boolean, String, Long, Float), Int]))
  checkAll("second tuple6", LensTests(second[(Int, Char, Boolean, String, Long, Float), Char]))
  checkAll("third tuple6" , LensTests(third[(Int, Char, Boolean, String, Long, Float), Boolean]))
  checkAll("fourth tuple6", LensTests(fourth[(Int, Char, Boolean, String, Long, Float), String]))
  checkAll("fifth tuple6" , LensTests(fifth[(Int, Char, Boolean, String, Long, Float), Long]))
  checkAll("sixth tuple6" , LensTests(sixth[(Int, Char, Boolean, String, Long, Float), Float]))

  checkAll("cons1 tuple6", Cons1Tests[(Int, Char, Boolean, String, Long, Float), Int, (Char, Boolean, String, Long, Float)])
  checkAll("snoc1 tuple6", Snoc1Tests[(Int, Char, Boolean, String, Long, Float), (Int, Char, Boolean, String, Long), Float])

}
