package monocle.std

import monocle.SimplePrism
import monocle.util.{Bits, Bounded}
import monocle.util.Bounded._
import scala.util.Try

trait IntInStances {

  implicit val intInstance: Bits[Int] with Bounded[Int] = new Bits[Int] with Bounded[Int] {

    val MaxValue: Int = Int.MaxValue
    val MinValue: Int = Int.MinValue

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

  val longToInt: SimplePrism[Long, Int] = safeCast(_.toLong, _.toInt)

  val stringToInt: SimplePrism[String, Int] = SimplePrism(
    _.toString,
    { s => Try{s.toInt}.toOption }
  )
}

object int extends IntInStances

