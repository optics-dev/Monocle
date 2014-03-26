package monocle.std

import monocle.SimplePrism
import scala.util.Try

object string extends StringInstances

trait StringInstances {
  def stringToBoolean: SimplePrism[String, Boolean] = SimplePrism(_.toString, s => Try(s.toBoolean).toOption)
  def stringToByte: SimplePrism[String, Byte] = SimplePrism(_.toString, s => Try(s.toByte).toOption)
  def stringToShort: SimplePrism[String, Short] = SimplePrism(_.toString, s => Try(s.toShort).toOption)
  def stringToInt: SimplePrism[String, Int] = SimplePrism(_.toString, s => Try(s.toInt).toOption)
  def stringToLong: SimplePrism[String, Long] = SimplePrism(_.toString, s => Try(s.toLong).toOption)
  def stringToFloat: SimplePrism[String, Float] = SimplePrism(_.toString, s => Try(s.toFloat).toOption)
  def stringToDouble: SimplePrism[String, Double] = SimplePrism(_.toString, s => Try(s.toDouble).toOption)
}
