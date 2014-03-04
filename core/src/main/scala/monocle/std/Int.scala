package monocle.std

import monocle.SimplePrism
import monocle.bits.Bits


trait IntInStances {

  val intToChar: SimplePrism[Int, Char] =
    SimplePrism[Int, Char](_.toInt, { n: Int => if (n > Char.MaxValue || n < Char.MinValue) None else Some(n.toChar) })

  implicit val intInstance: Bits[Int] = new Bits[Int] {

    val bitSize: Int = 32

    def bitwiseOr(a1: Int, a2: Int) : Int = a1 | a2
    def bitwiseAnd(a1: Int, a2: Int): Int = a1 & a2
    def bitwiseXor(a1: Int, a2: Int): Int = a1 ^ a2

    def singleBit(n: Int): Int = 1 << n

    def shiftL(a: Int, n: Int): Int = a << n
    def shiftR(a: Int, n: Int): Int = a >> n


    def testBit(a: Int, n: Int): Boolean = bitwiseAnd(a, singleBit(n)) != 0

    def signed(a: Int): Boolean = a.signum > 0

    def negate(a: Int): Int = ~a
  }

}

object int extends IntInStances
