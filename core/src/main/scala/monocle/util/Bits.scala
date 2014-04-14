package monocle.util


trait Bits[A] {

  def bitSize: Int

  def bitwiseAnd(a1: A, a2: A): A
  def bitwiseOr(a1: A, a2: A): A
  def bitwiseXor(a1: A, a2: A): A

  def shiftL(a: A, n: Int): A
  def shiftR(a: A, n: Int): A

  // create an A with a single bit set at position n
  def singleBit(n: Int): A

  def updateBit(a: A, n: Int, newValue: Boolean): A = if (newValue) setBit(a, n) else clearBit(a, n)

  def setBit(a: A, n: Int): A = bitwiseOr(a, singleBit(n))
  def clearBit(a: A, n: Int): A = bitwiseAnd(a, negate(singleBit(n)))

  def testBit(a: A, n: Int): Boolean

  def negate(a: A): A
  def signed(a: A): Boolean

}

object Bits extends BitsInstances {

  def apply[A](implicit ev: Bits[A]): Bits[A] = ev

}

trait BitsInstances {

  implicit val booleanBits = new Bits[Boolean] {

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

  implicit val byteBits = new Bits[Byte] {

    val bitSize: Int = 8

    def bitwiseOr(a1: Byte, a2: Byte) : Byte = (a1 | a2).toByte
    def bitwiseAnd(a1: Byte, a2: Byte): Byte = (a1 & a2).toByte
    def bitwiseXor(a1: Byte, a2: Byte): Byte = (a1 ^ a2).toByte

    def singleBit(n: Int): Byte = (1 << n).toByte

    def shiftL(a: Byte, n: Int): Byte = (a << n).toByte
    def shiftR(a: Byte, n: Int): Byte = (a >> n).toByte


    def testBit(a: Byte, n: Int): Boolean = bitwiseAnd(a, singleBit(n)) != 0

    def signed(a: Byte): Boolean = a.signum > 0

    def negate(a: Byte): Byte = (~a).toByte
  }

  implicit val charBits = new Bits[Char] {

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

  implicit val intBits = new Bits[Int] {

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

  implicit val longBits = new Bits[Long] {
    def signed(a: Long): Boolean = a.signum > 0
    def negate(a: Long): Long = ~a
    def testBit(a: Long, n: Int): Boolean = bitwiseAnd(a, singleBit(n)) != 0
    def singleBit(n: Int): Long = 1 << n

    def shiftR(a: Long, n: Int): Long = a >> n
    def shiftL(a: Long, n: Int): Long = a << n
    def bitwiseXor(a1: Long, a2: Long): Long = a1 ^ a2
    def bitwiseOr(a1: Long, a2: Long) : Long = a1 | a2
    def bitwiseAnd(a1: Long, a2: Long): Long = a1 & a2

    val bitSize: Int = 32
  }

}
