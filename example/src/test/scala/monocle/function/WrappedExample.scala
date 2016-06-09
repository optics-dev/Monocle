package monocle.function

import monocle.MonocleSuite
import monocle.function.all.{ala, unwrapped, wrapped}

import scalaz.{@@, Tags}
import scalaz.std.anyVal._
import scalaz.std.list._
import scalaz.std.function._
import scalaz.syntax.foldable._

class WrappedExample extends MonocleSuite {

  test("wrapped is an Iso") {
    (Tags.Max(100) applyIso wrapped[Int @@ Tags.Max, Int] get) shouldEqual 100
  }

  test("unwrapped is an Iso") {
    ("Hello" applyIso unwrapped[String @@ Tags.Dual, String] get) shouldEqual Tags.Dual("Hello")
  }

  test("ala wraps and unwraps inside of a functor") {
    ala[Boolean @@ Tags.Disjunction, Boolean, ({type λ[α] = List[Boolean] => α})#λ](f => xs => xs.foldMap(f)).apply(List(true, false, true)) shouldEqual true
  }

}
