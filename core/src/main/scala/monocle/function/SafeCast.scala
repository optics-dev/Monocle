package monocle.function

import monocle.SimplePrism
import monocle.util.Bounded
import monocle.SimplePrism._
import monocle.syntax._


trait SafeCast[S, A] {
  
  def safeCast: SimplePrism[S, A]

}

object SafeCast extends SafeCastInstances

trait SafeCastInstances {
  
  def safeCast[S, A](implicit ev: SafeCast[S, A]): SimplePrism[S, A] = ev.safeCast

  def orderingBoundedSafeCast[S: Ordering, A: Bounded](revCast: A => S, unsafeCast: S => A): SafeCast[S, A] = new SafeCast[S, A] {
    def safeCast = SimplePrism[S, A](revCast, { from: S =>
      val ord = implicitly[Ordering[S]]
      if (ord.gt(from, revCast(Bounded[A].MaxValue)) ||
          ord.lt(from, revCast(Bounded[A].MinValue))) None else Some(unsafeCast(from))
    })
  }

  implicit val longToInt : SafeCast[Long, Int]  = orderingBoundedSafeCast(_.toLong, _.toInt)
  implicit val longToChar: SafeCast[Long, Char] = orderingBoundedSafeCast(_.toInt, _.toChar)
  implicit val longToByte: SafeCast[Long, Byte] = orderingBoundedSafeCast(_.toLong, _.toByte)

  implicit val intToChar : SafeCast[Int, Char]  = orderingBoundedSafeCast(_.toInt, _.toChar)
  implicit val intToByte : SafeCast[Int, Byte]  = orderingBoundedSafeCast(_.toInt, _.toByte)

  implicit val longToBoolean: SafeCast[Long, Boolean] = orderingBoundedSafeCast(booleanToSigned, {
    case 0 => false
    case 1 => true
  })

  implicit val intToBoolean: SafeCast[Int, Boolean] = orderingBoundedSafeCast(booleanToSigned, {
    case 0 => false
    case 1 => true
  })

  implicit val charToBoolean: SafeCast[Char, Boolean] = orderingBoundedSafeCast(booleanToUnSigned, {
    case 0 => false
    case 1 => true
  })

  implicit val byteToBoolean: SafeCast[Byte, Boolean] = orderingBoundedSafeCast(booleanToSigned, {
    case 0 => false
    case 1 => true
  })

  // conversion to smallest signed primitive type
  private def booleanToSigned(b: Boolean): Byte = if(b) 1 else 0
  // conversion to smallest unsigned primitive type
  private def booleanToUnSigned(b: Boolean): Char = if(b) 1 else 0

  implicit val stringToBoolean = new SafeCast[String, Boolean] {
    def safeCast = trySimplePrism[String, Boolean](_.toString, _.toBoolean)
  }

  implicit val stringToLong = new SafeCast[String, Long] {
    def safeCast = SimplePrism[String, Long](_.toString, monocle.std.string.parseLong)
  }

  implicit val stringToInt = new SafeCast[String, Int] {
    def safeCast = stringToLong.safeCast <-? longToInt.safeCast
  }

  implicit val stringToByte = new SafeCast[String, Byte] {
    def safeCast = stringToLong.safeCast <-? longToByte.safeCast
  }

}

