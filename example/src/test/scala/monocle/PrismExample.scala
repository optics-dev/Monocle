package monocle

import monocle.macros.{GenIso, GenPrism}
import monocle.std._
import org.specs2.scalaz.Spec

import scalaz._


class PrismExample extends Spec {

  sealed trait IntOrString
  case class I(i: Int) extends IntOrString
  case class S(s: String) extends IntOrString

  val _i = GenPrism[IntOrString, I] composeIso GenIso[I, Int]
  val _s = Prism[IntOrString, String]{case S(s) => Maybe.just(s); case _ => Maybe.empty}(S.apply)

  "getMaybe return the target of a Prism, as it name implies, it can fail" in {
    _i.getMaybe(I(1))  ==== Maybe.just(1)
    _i.getMaybe(S("")) ==== Maybe.empty

    _s.getMaybe(S("hello")) ==== Maybe.just("hello")
    _s.getMaybe(I(10))      ==== Maybe.empty
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
    _s.modifyMaybe(_.reverse)(S("hello")) ==== Maybe.just(S("olleh"))
    _s.modifyMaybe(_.reverse)(I(3))       ==== Maybe.empty
  }


  "intToChar is a Prism from Int to Char" in {
    intToChar.getMaybe(65)    ==== Maybe.just('A')
    intToChar.reverseGet('a') ==== 97
  }

  "doubleToInt is a Prism from Double to Int" in {
    doubleToInt.getMaybe(5d) ==== Maybe.just(5)

    doubleToInt.getMaybe(5.4d)                    ==== Maybe.empty
    doubleToInt.getMaybe(Double.PositiveInfinity) ==== Maybe.empty
    doubleToInt.getMaybe(Double.NaN)              ==== Maybe.empty
  }

  "stringToInt is a Prism from String to Int" in {
    stringToInt.getMaybe("352")  ==== Maybe.just(352)
    stringToInt.getMaybe("-352") ==== Maybe.just(-352)
    stringToInt.getMaybe("୨")    ==== Maybe.empty // Non ascii digits
    stringToInt.getMaybe("")     ==== Maybe.empty
    // we reject case where String starts with +, otherwise it will be an invalid Prism according 2nd Prism law
    stringToInt.getMaybe("+352") ==== Maybe.empty

    stringToInt.reverseGet(8921)  ==== "8921"
    stringToInt.reverseGet(-32)   ==== "-32"

    stringToInt.modify(_ * 2)("1024") ==== "2048"
  }

  "stringToBoolean is a Prism from String to Boolean" in {
    stringToBoolean.getMaybe("true")  ==== Maybe.just(true)
    stringToBoolean.reverseGet(false) ==== "false"
  }

}
