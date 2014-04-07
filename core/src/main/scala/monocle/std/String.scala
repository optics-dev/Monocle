package monocle.std

import monocle.util.TryPrism._

object string extends StringInstances

trait StringInstances {
  def stringCast[A](unsafe: String => A) = trySimplePrism[String, A](_.toString, unsafe)

  def stringToBoolean = stringCast(_.toBoolean)
  def stringToByte = stringCast(_.toByte)
  def stringToShort = stringCast(_.toShort)
  def stringToInt = stringCast(_.toInt)
  def stringToLong = stringCast(_.toLong)
  def stringToFloat = stringCast(_.toFloat)
  def stringToDouble = stringCast(_.toDouble)
}
