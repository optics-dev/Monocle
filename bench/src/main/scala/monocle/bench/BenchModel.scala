package monocle.bench

import scala.util.Random
import scalaz._
import scalaz.std.anyVal._


object BenchModel {

  def safeDivide(a: Int, b: Int): Maybe[Int] = if(b == 0) Maybe.empty else Maybe.just(a / b)

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


  def genNested0(): Nested0 = Nested0(genStr(), genInt(),
    Nested1(genStr(), genInt(),
      Nested2(genStr(), genInt(),
        Nested3(genStr(), genInt(),
          Nested4(genStr(), genInt(),
            Nested5(genStr(), genInt(),
              Nested6(genStr(), genInt())
              ,genLong()), genLong()), genLong()), genLong()), genLong()), genLong())

  val n0s: Array[Nested0] = (1 to 10).map( _ => genNested0()).toArray

  val n0 = genNested0()

  sealed trait ADT
  case class I(i: Int)    extends ADT
  case class S(s: String) extends ADT
  case class R(r: ADT)    extends ADT

  def getIMaybe(adt: ADT): Maybe[Int]    = adt match { case I(i) => Maybe.just(i); case _ => Maybe.empty }
  def getSMaybe(adt: ADT): Maybe[String] = adt match { case S(s) => Maybe.just(s); case _ => Maybe.empty }
  def getRMaybe(adt: ADT): Maybe[ADT]    = adt match { case R(r) => Maybe.just(r); case _ => Maybe.empty }

  def mkI(i: Int)   : ADT = I(i)
  def mkS(s: String): ADT = S(s)
  def mkR(r: ADT)   : ADT = R(r)

  val adt0 = mkI(5)
  val adt3 = mkR(mkR(mkR(mkI(5))))
  val adt6 = mkR(mkR(mkR(mkR(mkR(mkR(mkI(5)))))))


  case class Point3(x: Int, y: Int, z: Int)
  val p = Point3(2, 10, 24)

  val iMap = IMap.fromList(Stream.from(1).take(200).map(_ -> 5).toList)
}
