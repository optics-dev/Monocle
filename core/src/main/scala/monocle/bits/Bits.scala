package monocle.bits

trait Bits[A] {

  def bitSize: Int

  def bitwiseAnd(a1: A, a2: A): A
  def bitwiseOr(a1: A, a2: A): A
  def bitwiseXor(a1: A, a2: A): A

  def shiftL(a: A, n: Int): A
  def shiftR(a: A, n: Int): A

  // create an A with a single bit set at position n
  def singleBit(n: Int): A

  def updateBit(a: A, n: Int, newValue: Boolean): A = if(newValue) setBit(a, n) else clearBit(a, n)

  def setBit(a: A, n: Int): A   = bitwiseOr(a, singleBit(n))
  def clearBit(a: A, n: Int): A = bitwiseAnd(a, negate(singleBit(n)))

  def testBit(a: A, n: Int): Boolean

  def negate(a: A): A
  def signed(a: A): Boolean

}

object Bits {

  def apply[A](implicit ev: Bits[A]): Bits[A] = ev

  implicit val intInstance : Bits[Int] = new Bits[Int] {

    def bitwiseOr(a1: Int, a2: Int): Int = a1 | a2

    def singleBit(n: Int): Int = 1 << n

    val bitSize: Int = 32

    def bitwiseAnd(a1: Int, a2: Int): Int = a1 & a2

    def shiftL(a: Int, n: Int): Int = a << n

    def bitwiseXor(a1: Int, a2: Int): Int = a1 ^ a2

    def testBit(a: Int, n: Int): Boolean = bitwiseAnd(a, singleBit(n)) != 0

    def shiftR(a: Int, n: Int): Int = a >> n

    def signed(a: Int): Boolean = a.signum > 0

    def negate(a: Int): Int = ~ a
  }

  // todo: can we avoid down casting ?
  implicit val charInstance : Bits[Char] = new Bits[Char] {

    val bitSize: Int = 16

    def bitwiseOr(a1: Char, a2: Char): Char = (a1 | a2).toChar
    def bitwiseAnd(a1: Char, a2: Char): Char = (a1 & a2).asInstanceOf[Char]
    def bitwiseXor(a1: Char, a2: Char): Char = (a1 ^ a2).asInstanceOf[Char]

    def shiftL(a: Char, n: Int): Char = (a << n).asInstanceOf[Char]
    def shiftR(a: Char, n: Int): Char = (a >> n).asInstanceOf[Char]

    def singleBit(n: Int): Char = (1 << n).asInstanceOf[Char]

    def testBit(a: Char, n: Int): Boolean = bitwiseAnd(a, singleBit(n)) != 0

    def negate(a: Char): Char = (~ a).asInstanceOf[Char]
    def signed(a: Char): Boolean = a.signum > 0
  }

}
