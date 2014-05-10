package monocle

import org.specs2.scalaz.Spec
import monocle.std.function._
import monocle.syntax._


class FunctionExample extends Spec {

  "curry curries a function" in {
    def f(a: Int, b: Int): Int = a + b

    (f _ <-> curry get)(1)(2) shouldEqual 3
  }

  "uncurry uncurries a function" in {
    def f(a: Int)(b: Int): Int = a + b

    (f _ <-> uncurry get)(1, 2) shouldEqual 3
  }

  "curry and uncurry should work with functions up to 5 arguments" in {
    def f(a: Int)(b: Int)(c: Int)(d: Int)(e: Int): Int =
      a + b + c + d + e

    (f _ <-> uncurry get)(1, 2, 3, 4, 5) shouldEqual 15
  }

  "If we compose with the curried function, it should also compose in the uncurried version" in {
    def f(a: Int, b: Int): Int =
      2 * a + 3 * b

    /**
     * Note: We can only stay in the same function type, because curry is a SimpleIso.
     * So we can't for example modify by applying the first argument.
     **/
    /**
     * Here we increase the first argument by one, and then apply the function,
     * Which is easier to do when the function is curried rather than uncurried,
     * so we do the modification through the Iso.
     **/
    (f _ <-> curry modify (_ compose (_ + 1)))(5, 7) shouldEqual (2 * 6 + 3 * 7)

  }

  "flip exchanges the the first 2 parameters of a function" in {
    def f(a: Int, b: Double): Double = a + b

    (f _ <-> curry <-> flip <-> uncurry get)(3.2, 1) shouldEqual 4.2
  }

  "Increase the second argument of a 2 argument function" in {
    def f(a: Int, b: Int): Int =
      2 * a + 3 * b

    /**
     * If we wanted to increase the second argument instead, we could use flip.
     */
    (f _ <-> curry <-> flip modify (_ compose (_ + 1)))(5, 7) shouldEqual (2 * 5 + 3 * 8)
  }

}
