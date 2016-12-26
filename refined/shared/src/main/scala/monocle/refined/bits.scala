package monocle.refined

import monocle.function.At
import monocle.refined.internal.Bits

object bits extends BitsInstances

trait BitsInstances {

  implicit val byteAt: At[Byte, ByteBits, Boolean] = fromBits[Byte, ByteBits](_.value)
  implicit val charAt: At[Char, CharBits, Boolean] = fromBits[Char, CharBits](_.value)
  implicit val intAt : At[Int , IntBits , Boolean] = fromBits[Int , IntBits ](_.value)
  implicit val longAt: At[Long, LongBits, Boolean] = fromBits[Long, LongBits](_.value)

  def fromBits[S, I](toInt: I => Int)(implicit S: Bits[S]): At[S, I, Boolean] =
    At[S, I, Boolean](i => s => S.testBit(s, toInt(i)))(i => a => s => S.updateBit(a)(s, toInt(i)))

}
