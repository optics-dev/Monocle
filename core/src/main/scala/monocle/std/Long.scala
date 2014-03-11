package monocle.std

import monocle.util.Bits

object long extends LongInstances

trait LongInstances {

  implicit val longInstance = new Bits[Long] {

    def signed(a: Long): Boolean = a.signum > 0

    def negate(a: Long): Long = ~a

    def testBit(a: Long, n: Int): Boolean = bitwiseAnd(a, singleBit(n)) != 0

    def singleBit(n: Int): Long = 1 << n

    def shiftR(a: Long, n: Int): Long = a >> n
    def shiftL(a: Long, n: Int): Long = a << n

    def bitwiseOr (a1: Long, a2: Long): Long = a1 | a2
    def bitwiseAnd(a1: Long, a2: Long): Long = a1 & a2
    def bitwiseXor(a1: Long, a2: Long): Long = a1 ^ a2

    val bitSize: Int = 32

  }

}
