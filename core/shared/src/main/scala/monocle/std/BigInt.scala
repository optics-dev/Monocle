package monocle.std

import monocle.Prism

object bigint extends BigIntOptics

trait BigIntOptics {
  val bigIntToLong: Prism[BigInt, Long] =
    Prism[BigInt, Long](bi => if (bi.isValidLong) Some(bi.longValue) else None)(BigInt(_))

  val bigIntToInt: Prism[BigInt, Int] =
    bigIntToLong composePrism long.longToInt

  val bigIntToChar: Prism[BigInt, Char] =
    bigIntToLong composePrism long.longToChar

  val bigIntToByte: Prism[BigInt, Byte] =
    bigIntToLong composePrism long.longToByte

  val bigIntToBoolean: Prism[BigInt, Boolean] =
    bigIntToLong composePrism long.longToBoolean
}
