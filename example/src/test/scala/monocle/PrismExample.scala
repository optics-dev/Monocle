package monocle

import monocle.macros.{GenIso, GenPrism}
import monocle.std._

class PrismExample extends MonocleSuite {

  sealed trait IntOrString
  case class I(i: Int) extends IntOrString
  case class S(s: String) extends IntOrString

  val _i = GenPrism[IntOrString, I] composeIso GenIso[I, Int]
  val _s = Prism[IntOrString, String]{case S(s) => Some(s); case _ => None}(S.apply)

  test("getOption return the target of a Prism, as it name implies, it can fail") {
    _i.getOption(I(1))  shouldEqual Some(1)
    _i.getOption(S("")) shouldEqual None

    _s.getOption(S("hello")) shouldEqual Some("hello")
    _s.getOption(I(10))      shouldEqual None
  }

  test("reverseGet return the source of Prism, in this case the constructor of IntOrString") {
    _i.reverseGet(3)     shouldEqual I(3)
    _s.reverseGet("Yop") shouldEqual S("Yop")
  }

  test("modify can alter the target of Prism, if there is no target modify has no effect") {
    _i.modify(_ + 1)(I(3))   shouldEqual I(4)
    _i.modify(_ + 1)(S(""))  shouldEqual S("")
  }

  test("modify returns the original object with its target modified or nothing if there is no target") {
    _s.modifyOption(_.reverse)(S("hello")) shouldEqual Some(S("olleh"))
    _s.modifyOption(_.reverse)(I(3))       shouldEqual None
  }


  test("intToChar is a Prism from Int to Char") {
    intToChar.getOption(65)    shouldEqual Some('A')
    intToChar.reverseGet('a') shouldEqual 97
  }

  test("doubleToInt is a Prism from Double to Int") {
    doubleToInt.getOption(5d) shouldEqual Some(5)

    doubleToInt.getOption(5.4d)                    shouldEqual None
    doubleToInt.getOption(Double.PositiveInfinity) shouldEqual None
    doubleToInt.getOption(Double.NaN)              shouldEqual None
  }

  test("stringToInt is a Prism from String to Int") {
    stringToInt.getOption("352")  shouldEqual Some(352)
    stringToInt.getOption("-352") shouldEqual Some(-352)
    stringToInt.getOption("୨")    shouldEqual None // Non ascii digits
    stringToInt.getOption("")     shouldEqual None
    // we reject case where String starts with +, otherwise it will be an invalid Prism according 2nd Prism law
    stringToInt.getOption("+352") shouldEqual None

    stringToInt.reverseGet(8921)  shouldEqual "8921"
    stringToInt.reverseGet(-32)   shouldEqual "-32"

    stringToInt.modify(_ * 2)("1024") shouldEqual "2048"
  }

  test("stringToBoolean is a Prism from String to Boolean") {
    stringToBoolean.getOption("true")  shouldEqual Some(true)
    stringToBoolean.reverseGet(false) shouldEqual "false"
  }

}
