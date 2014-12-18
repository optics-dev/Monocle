package monocle.bench

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

  val n0 = Nested0("plop", 45,
    Nested1("Hello", -678,
      Nested2("World", 0,
        Nested3("Yoooo", 42,
          Nested4("Yo", 9999,
            Nested5("WoooooooooooooooW", 76,
              Nested6("", 0)
              ,0), -990993L), 12345L), 123456789L), 342072347L), 6789L)



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
