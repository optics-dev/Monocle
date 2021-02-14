package monocle.std

import monocle.Prism

object bigint extends BigIntOptics

trait BigIntOptics {
  val bigIntToLong: Prism[BigInt, Long] =
    Prism[BigInt, Long](bi => if (bi.isValidLong) Some(bi.longValue) else None)(BigInt(_))

  val bigIntToInt: Prism[BigInt, Int] =
    bigIntToLong.andThen(long.longToInt)

  val bigIntToChar: Prism[BigInt, Char] =
    bigIntToLong.andThen(long.longToChar)

  val bigIntToByte: Prism[BigInt, Byte] =
    bigIntToLong.andThen(long.longToByte)

  val bigIntToBoolean: Prism[BigInt, Boolean] =
    bigIntToLong.andThen(long.longToBoolean)
}
