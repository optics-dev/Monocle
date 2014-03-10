package monocle.std

import monocle.util.{ Bounded, Bits }
import monocle._
import monocle.util.Bounded._

trait BooleanInstances {

  implicit val booleanInstance = new Bits[Boolean] with Bounded[Boolean] {

    val MaxValue: Boolean = true
    val MinValue: Boolean = false

    val bitSize: Int = 1

    def bitwiseOr(a1: Boolean, a2: Boolean): Boolean = a1 | a2
    def bitwiseAnd(a1: Boolean, a2: Boolean): Boolean = a1 & a2
    def bitwiseXor(a1: Boolean, a2: Boolean): Boolean = a1 ^ a2

    def singleBit(n: Int): Boolean = true

    def shiftL(a: Boolean, n: Int): Boolean = false
    def shiftR(a: Boolean, n: Int): Boolean = false

    def testBit(a: Boolean, n: Int): Boolean = a

    def signed(a: Boolean): Boolean = a

    def negate(a: Boolean): Boolean = !a
  }

  // conversion to smallest signed primitive type
  private def booleanToSigned(b: Boolean): Byte = if (b) 1 else 0
  // conversion to smallest unsigned primitive type
  private def booleanToUnSigned(b: Boolean): Char = if (b) 1 else 0

  // todo: can we generalise the primitive type to Boolean conversion
  val longToBoolean: SimplePrism[Long, Boolean] = safeCast(booleanToSigned, {
    case 0 => false
    case 1 => true
  })

  val intToBoolean: SimplePrism[Int, Boolean] = safeCast(booleanToSigned, {
    case 0 => false
    case 1 => true
  })

  val charToBoolean: SimplePrism[Char, Boolean] = safeCast(booleanToUnSigned, {
    case 0 => false
    case 1 => true
  })

  val byteToBoolean: SimplePrism[Byte, Boolean] = safeCast(booleanToSigned, {
    case 0 => false
    case 1 => true
  })

}

object boolean extends BooleanInstances
