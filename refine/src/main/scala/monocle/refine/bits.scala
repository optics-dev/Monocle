package monocle.refine

import monocle.function.At
import monocle.refine.internal.Bits

object bits extends BitsInstances

trait BitsInstances {

  implicit val byteAt: At[Byte, ByteBits, Boolean] = fromBits[Byte, ByteBits](_.get)
  implicit val charAt: At[Char, CharBits, Boolean] = fromBits[Char, CharBits](_.get)
  implicit val intAt : At[Int , IntBits , Boolean] = fromBits[Int , IntBits ](_.get)
  implicit val longAt: At[Long, LongBits, Boolean] = fromBits[Long, LongBits](_.get)

  def fromBits[S, I](toInt: I => Int)(implicit S: Bits[S]): At[S, I, Boolean] =
    At[S, I, Boolean](i => s => S.testBit(s, toInt(i)))(i => a => s => S.updateBit(a)(s, toInt(i)))

}
