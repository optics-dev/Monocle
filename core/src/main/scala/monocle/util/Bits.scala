package monocle.util

import monocle._

trait Bits[A] {

  def bitSize: Int

  def bitwiseOr(a1: A, a2: A): A
  def bitwiseAnd(a1: A, a2: A): A
  def bitwiseXor(a1: A, a2: A): A

  def shiftL(a: A, n: Int): A
  def shiftR(a: A, n: Int): A

  // create an A with a single bit set at position n
  def singleBit(n: Int): A

  def updateBit(a: A, n: Int, newValue: Boolean): A = if (newValue) setBit(a, n) else clearBit(a, n)

  def setBit  (a: A, n: Int): A = bitwiseOr (a,        singleBit(n) )
  def clearBit(a: A, n: Int): A = bitwiseAnd(a, negate(singleBit(n)))

  def testBit(a: A, n: Int): Boolean

  def negate(a: A): A
  def signed(a: A): Boolean

}

object Bits {

  def apply[A](implicit ev: Bits[A]): Bits[A] = ev

  def atBit[S: Bits](n: Int): SimpleLens[S, Boolean] = {
    val bitsInstance = Bits[S]
    val index = normalizeIndex(bitsInstance.bitSize, n)
    SimpleLens[S, Boolean](bitsInstance.testBit(_, index), bitsInstance.updateBit(_, index, _))
  }

  // map i to a value in base, negative value means that it is indexed from the end
  private def normalizeIndex(base: Int, i: Int): Int = {
    val modulo = i % base
    if (modulo >= 0) modulo else modulo + base
  }

}
