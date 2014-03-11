package monocle.std

import monocle.util.{ Bounded, Bits }
import monocle._
import monocle.util.Bounded._

trait ByteInstances {

  implicit val byteInstance = new Bits[Byte] with Bounded[Byte] {

    val MaxValue: Byte = Byte.MaxValue
    val MinValue: Byte = Byte.MinValue

    val bitSize: Int = 8

    def bitwiseOr (a1: Byte, a2: Byte): Byte = (a1 | a2).toByte
    def bitwiseAnd(a1: Byte, a2: Byte): Byte = (a1 & a2).toByte
    def bitwiseXor(a1: Byte, a2: Byte): Byte = (a1 ^ a2).toByte

    def singleBit(n: Int): Byte = (1 << n).toByte

    def shiftL(a: Byte, n: Int): Byte = (a << n).toByte
    def shiftR(a: Byte, n: Int): Byte = (a >> n).toByte

    def testBit(a: Byte, n: Int): Boolean = bitwiseAnd(a, singleBit(n)) != 0

    def signed(a: Byte): Boolean = a.signum > 0

    def negate(a: Byte): Byte = (~a).toByte
  }

  val intToByte : SimplePrism[Int,  Byte] = safeCast(_.toInt , _.toByte)
  val longToByte: SimplePrism[Long, Byte] = safeCast(_.toLong, _.toByte)

}

object byte extends ByteInstances
