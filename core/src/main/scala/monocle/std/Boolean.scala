package monocle.std

import monocle.SimplePrism
import monocle.bits.Bits


trait BooleanInstances {

  implicit val booleanInstance: Bits[Boolean] = new Bits[Boolean] {

    val bitSize: Int = 1

    def bitwiseOr(a1: Boolean, a2: Boolean) : Boolean = a1 | a2
    def bitwiseAnd(a1: Boolean, a2: Boolean): Boolean = a1 & a2
    def bitwiseXor(a1: Boolean, a2: Boolean): Boolean = a1 ^ a2

    def singleBit(n: Int): Boolean = true

    def shiftL(a: Boolean, n: Int): Boolean = false
    def shiftR(a: Boolean, n: Int): Boolean = false


    def testBit(a: Boolean, n: Int): Boolean = a

    def signed(a: Boolean): Boolean = a

    def negate(a: Boolean): Boolean = !a
  }

}

object boolean extends BooleanInstances
