package monocle.refine

import eu.timepit.refined._
import monocle.function.At
import monocle.refine.internal.Bits

object bits extends BitsInstances

trait BitsInstances {

  implicit val byteAt: At[Byte, ZeroTo[W.`7`.T], Boolean]   = fromBits[Byte, ZeroTo[W.`7`.T]](_.get)
  implicit val charAt: At[Char, ZeroTo[W.`15`.T], Boolean]  = fromBits[Char, ZeroTo[W.`15`.T]](_.get)
  implicit val intAt : At[Int, ZeroTo[W.`31`.T], Boolean]   = fromBits[Int, ZeroTo[W.`31`.T]](_.get)
  implicit val longAt: At[Long, ZeroTo[W.`63`.T], Boolean]  = fromBits[Long, ZeroTo[W.`63`.T]](_.get)

  def fromBits[S, I](toInt: I => Int)(implicit S: Bits[S]): At[S, I, Boolean] =
    At[S, I, Boolean](i => s => S.testBit(s, toInt(i)))(i => a => s => S.updateBit(a)(s, toInt(i)))

}
