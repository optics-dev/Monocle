package monocle

import monocle.macros.{GenIso, GenPrism}
import monocle.std._
import org.specs2.scalaz.Spec

class PrismExample extends Spec {

  sealed trait IntOrString
  case class I(i: Int) extends IntOrString
  case class S(s: String) extends IntOrString

  val _i = GenPrism[IntOrString, I] composeIso GenIso[I, Int]
  val _s = Prism[IntOrString, String]{case S(s) => Some(s); case _ => None}(S.apply)

  "getOption return the target of a Prism, as it name implies, it can fail" in {
    _i.getOption(I(1))  ==== Some(1)
    _i.getOption(S("")) ==== None

    _s.getOption(S("hello")) ==== Some("hello")
    _s.getOption(I(10))      ==== None
  }

  "reverseGet return the source of Prism, in this case the constructor of IntOrString" in {
    _i.reverseGet(3)     ==== I(3)
    _s.reverseGet("Yop") ==== S("Yop")
  }

  "modify can alter the target of Prism, if there is no target modify has no effect" in {
    _i.modify(_ + 1)(I(3))   ==== I(4)
    _i.modify(_ + 1)(S(""))  ==== S("")
  }

  "modify returns the original object with its target modified or nothing if there is no target" in {
    _s.modifyOption(_.reverse)(S("hello")) ==== Some(S("olleh"))
    _s.modifyOption(_.reverse)(I(3))       ==== None
  }


  "intToChar is a Prism from Int to Char" in {
    intToChar.getOption(65)    ==== Some('A')
    intToChar.reverseGet('a') ==== 97
  }

  "doubleToInt is a Prism from Double to Int" in {
    doubleToInt.getOption(5d) ==== Some(5)

    doubleToInt.getOption(5.4d)                    ==== None
    doubleToInt.getOption(Double.PositiveInfinity) ==== None
    doubleToInt.getOption(Double.NaN)              ==== None
  }

  "stringToInt is a Prism from String to Int" in {
    stringToInt.getOption("352")  ==== Some(352)
    stringToInt.getOption("-352") ==== Some(-352)
    stringToInt.getOption("୨")    ==== None // Non ascii digits
    stringToInt.getOption("")     ==== None
    // we reject case where String starts with +, otherwise it will be an invalid Prism according 2nd Prism law
    stringToInt.getOption("+352") ==== None

    stringToInt.reverseGet(8921)  ==== "8921"
    stringToInt.reverseGet(-32)   ==== "-32"

    stringToInt.modify(_ * 2)("1024") ==== "2048"
  }

  "stringToBoolean is a Prism from String to Boolean" in {
    stringToBoolean.getOption("true")  ==== Some(true)
    stringToBoolean.reverseGet(false) ==== "false"
  }

}
