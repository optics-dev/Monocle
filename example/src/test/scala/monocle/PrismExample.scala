package monocle

import monocle.std._
import org.specs2.scalaz.Spec

import scalaz.Tags.Multiplication
import scalaz._
import scalaz.std.anyVal._


class PrismExample extends Spec {

  sealed trait Expression[T]
  case class Atom[T](value: T) extends Expression[T]
  case class Plus[T](left: Expression[T], right: Expression[T]) extends Expression[T]

  def atom[A, B: Monoid] = PPrism[Expression[A], Expression[B], A, B]{
    case Atom(v)    => \/-(v)
    case Plus(_, _) => -\/(Atom(Monoid[B].zero))
  }(Atom.apply[B])

  val mAtom = atom[Int, Int @@ Multiplication]

  "Prism getMaybe extracts the target (A) of a Prism if it exists" in {
    mAtom.getMaybe(Atom(3))                ==== Maybe.just(3)
    mAtom.getMaybe(Plus(Atom(1), Atom(2))) ==== Maybe.empty
  }

  "Prism getOrModify extracts the target (A) of a Prism if it exists or it modifies the source (T)" in {
    mAtom.getOrModify(Atom(3))                ==== \/-(3)
    mAtom.getOrModify(Plus(Atom(1), Atom(2))) ==== -\/(Atom(Multiplication(1)))
  }

  "Prism reverseGet transforms the modified target (B) in a modified source (T)" in {
    mAtom.reverseGet(Multiplication(1)) ==== Atom(Multiplication(1))
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
