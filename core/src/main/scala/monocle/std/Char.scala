package monocle.std

import monocle.bits.Bits


trait CharInstances {

  implicit val charInstance: Bits[Char] = new Bits[Char] {

    val bitSize: Int = 16

    def bitwiseOr(a1: Char, a2: Char): Char  = (a1 | a2).toChar
    def bitwiseAnd(a1: Char, a2: Char): Char = (a1 & a2).toChar
    def bitwiseXor(a1: Char, a2: Char): Char = (a1 ^ a2).toChar

    def shiftL(a: Char, n: Int): Char = (a << n).toChar
    def shiftR(a: Char, n: Int): Char = (a >> n).toChar

    def singleBit(n: Int): Char = (1 << n).toChar

    def testBit(a: Char, n: Int): Boolean = bitwiseAnd(a, singleBit(n)) != 0

    def negate(a: Char): Char = (~a).toChar
    def signed(a: Char): Boolean = a.signum > 0
  }

}

object char extends CharInstances