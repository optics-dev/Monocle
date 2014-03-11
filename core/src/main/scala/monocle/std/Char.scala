package monocle.std

import monocle.SimplePrism
import monocle.util.{ Bits, Bounded }
import monocle.util.Bounded._

trait CharInstances {

  implicit val charInstance = new Bits[Char] with Bounded[Char] {

    val MaxValue: Char = Char.MaxValue
    val MinValue: Char = Char.MinValue

    val bitSize: Int = 16

    def bitwiseOr (a1: Char, a2: Char): Char = (a1 | a2).toChar
    def bitwiseAnd(a1: Char, a2: Char): Char = (a1 & a2).toChar
    def bitwiseXor(a1: Char, a2: Char): Char = (a1 ^ a2).toChar

    def shiftL(a: Char, n: Int): Char = (a << n).toChar
    def shiftR(a: Char, n: Int): Char = (a >> n).toChar

    def singleBit(n: Int): Char = (1 << n).toChar

    def testBit(a: Char, n: Int): Boolean = bitwiseAnd(a, singleBit(n)) != 0

    def negate(a: Char): Char = (~a).toChar
    def signed(a: Char): Boolean = a.signum > 0
  }

  val longToChar: SimplePrism[Long, Char] = safeCast(_.toInt, _.toChar)
  val intToChar : SimplePrism[Int,  Char] = safeCast(_.toInt, _.toChar)

}

object char extends CharInstances