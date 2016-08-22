package monocle.bench

import scala.util.Random
import scalaz._
import scalaz.std.anyVal._


object BenchModel {

  def halfEven(n: Int): Option[Int] =
    if(n % 2 == 0) Some(n / 2)
    else None

  case class Nested0(s: String, i: Int, n: Nested1, l: Long)
  case class Nested1(s: String, i: Int, n: Nested2, l: Long)
  case class Nested2(s: String, i: Int, n: Nested3, l: Long)
  case class Nested3(s: String, i: Int, n: Nested4, l: Long)
  case class Nested4(s: String, i: Int, n: Nested5, l: Long)
  case class Nested5(s: String, i: Int, n: Nested6, l: Long)
  case class Nested6(s: String, i: Int)

  val r = new Random

  def genInt(): Int = r.nextInt()
  def genLong(): Long = r.nextLong()
  def genStr(): String = r.nextString(r.nextInt(100))

  sealed trait ADT
  case class I(i: Int)    extends ADT
  case class R(r: ADT)    extends ADT

  def getIOption(adt: ADT): Option[Int]    = adt match { case I(i) => Some(i); case _ => None }
  def getROption(adt: ADT): Option[ADT]    = adt match { case R(r) => Some(r); case _ => None }

  def mkI(i: Int)   : ADT = I(i)
  def mkR(r: ADT)   : ADT = R(r)


  case class Point3(x: Int, y: Int, z: Int)
  val p = Point3(2, 10, 24)

  val iMap = IMap.fromList(Stream.from(1).take(200).map(_ -> 5).toList)


  case class IntWrapper0(i: Int)
  case class IntWrapper1(i: Int)
  case class IntWrapper2(i: Int)
  case class IntWrapper3(i: Int)
  case class IntWrapper4(i: Int)
  case class IntWrapper5(i: Int)
  case class IntWrapper6(i: Int)

  val i = genInt()
  val w0 = IntWrapper0(i)
  val w3 = IntWrapper0(i)
  val w6 = IntWrapper6(i)

}
