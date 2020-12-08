package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function.{Cons1Tests, EachTests, IndexTests}
import cats.data.OneAnd

import scala.annotation.nowarn

class OneAndSpec extends MonocleSuite {
  import cats.laws.discipline.arbitrary._

  checkAll("each OneAnd", EachTests[OneAnd[List, Int], Int])
  checkAll("index OneAnd", IndexTests[OneAnd[List, Int], Int, Int])
  checkAll("cons1 OneAnd", Cons1Tests[OneAnd[List, Int], Int, List[Int]]): @nowarn
}
