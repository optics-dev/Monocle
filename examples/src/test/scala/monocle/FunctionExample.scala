package monocle

import org.specs2.scalaz.Spec
import monocle.std.function._
import monocle.syntax.iso._


class FunctionExample extends Spec {

  "curry curries the the first 2 parameters of a function" in {
    def f(a: Int, b: Int): Int = a + b

    (f _ <-> curry get)(1)(2) shouldEqual 3
  }

  "uncurry uncurries the the first 2 parameters of a function" in {
    def f(a: Int)(b: Int): Int = a + b

    (f _ <-> uncurry get)(1, 2) shouldEqual 3
  }

  "flip exchanges the the first 2 parameters of a function" in {
    def f(a: Int, b: Double): Double = a + b

    (f _ <-> curry <-> flip <-> uncurry get)(3.2, 1) shouldEqual 4.2
  }

}
