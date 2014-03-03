package monocle

package object bits {

  def atBit[S: Bits](n: Int): Lens[S, S, Boolean, Boolean] = {
    val bitsInstance = Bits[S]
    val index = normalizeIndex(bitsInstance.bitSize, n)
    Lens[S, S, Boolean, Boolean](bitsInstance.testBit(_, index), bitsInstance.updateBit(_, index, _))
  }

  // map i to a value in base, negative value means that it is indexed from the end
  private def normalizeIndex(base: Int, i: Int): Int = {
    val modulo = i % base
    if (modulo >= 0) modulo else modulo + base
  }

}
