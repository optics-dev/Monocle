package monocle.function

import monocle.SimpleLens
import monocle.internal.Bits

trait AtBit[S] {

  def atBit(index: Int): SimpleLens[S, Boolean]

}

object AtBit extends AtBitFunctions

trait AtBitFunctions {

  def atBit[S](index: Int)(implicit ev: AtBit[S]): SimpleLens[S, Boolean] = ev.atBit(index)

  def bitsAtBit[S: Bits]: AtBit[S] = new AtBit[S] {
    def atBit(index: Int): SimpleLens[S, Boolean] = {
      val n = normalizeIndex(Bits[S].bitSize, index)
      SimpleLens[S, Boolean](Bits[S].testBit(_, n), Bits[S].updateBit(_, n, _))
    }
  }

  // map i to a value in base, negative value means that it is indexed from the end
  private def normalizeIndex(base: Int, i: Int): Int = {
    val modulo = i % base
    if (modulo >= 0) modulo else modulo + base
  }

}
