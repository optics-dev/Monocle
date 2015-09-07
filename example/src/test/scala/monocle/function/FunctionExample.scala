package monocle.function

import monocle.MonocleSuite

class FunctionExample extends MonocleSuite {

  test("curry curries a function") {
    def f(a: Int, b: Int): Int = a + b

    (f _ applyIso curry get)(1)(2) shouldEqual 3
  }

  test("uncurry uncurries a function") {
    def f(a: Int)(b: Int): Int = a + b

    (f _ applyIso uncurry get)(1, 2) shouldEqual 3
  }

  test("curry and uncurry should work with functions up to 5 arguments") {
    def f(a: Int)(b: Int)(c: Int)(d: Int)(e: Int): Int =
      a + b + c + d + e

    (f _ applyIso uncurry get)(1, 2, 3, 4, 5) shouldEqual 15
  }

  test("If we compose with the curried function, it should also compose in the uncurried version") {
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
    (f _ applyIso curry modify (_ compose (_ + 1)))(5, 7) shouldEqual (2 * 6 + 3 * 7)

  }

  test("flip exchanges the the first 2 parameters of a function") {
    def f(a: Int, b: Double): Double = a + b

    (f _ applyIso curry composeIso flip composeIso uncurry get)(3.2, 1) shouldEqual 4.2
  }

  test("Increase the second argument of a 2 argument function") {
    def f(a: Int, b: Int): Int =
      2 * a + 3 * b

    /**
     * If we wanted to increase the second argument instead, we could use flip.
     */
    (f _ applyIso curry composeIso flip modify (_ compose (_ + 1)))(5, 7) shouldEqual (2 * 5 + 3 * 8)
  }

}
