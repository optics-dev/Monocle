package monocle.function

import monocle._
import org.scalacheck.Arbitrary

import scalaz.{NonEmptyList, Equal}
import scalaz.syntax.applicative._

case class MMap[K, V](map: Map[K, V])

object MMap {
  def toMap[K, V]: Iso[MMap[K, V], Map[K, V]] =
    Iso[MMap[K, V], Map[K, V]](_.map)(MMap(_))

  implicit def mmapEq[K, V]: Equal[MMap[K, V]] = Equal.equalA
  implicit def mmapArb[K: Arbitrary, V: Arbitrary]: Arbitrary[MMap[K, V]] =
    Arbitrary(Arbitrary.arbitrary[Map[K, V]].map(MMap(_)))
}

case class CNel(head: Char, tail: List[Char])

object CNel extends TestInstances {
  val toNel: Iso[CNel, NonEmptyList[Char]] =
    Iso[CNel, NonEmptyList[Char]](c => NonEmptyList(c.head, c.tail: _*))(n => CNel(n.head, n.tail))

  implicit val cNelEq: Equal[CNel] = Equal.equalA
  implicit val cNelArb: Arbitrary[CNel] = Arbitrary(^(Arbitrary.arbitrary[Char], Arbitrary.arbitrary[List[Char]])(CNel.apply))
}

case class CList(list: List[Char])

object CList {
  val toList: Iso[CList, List[Char]] = Iso[CList, List[Char]](_.list)(CList(_))

  implicit val clistEq: Equal[CList] = Equal.equalA
  implicit val slistQrb: Arbitrary[CList] = Arbitrary(Arbitrary.arbitrary[List[Char]].map(CList(_)))
}

case class Raw(b: Boolean, c: Char, i: Int, l: Long, f: Float, d: Double)

object Raw extends TestInstances {
  val toTuple: Iso[Raw, (Boolean, Char, Int, Long, Float, Double)] =
    Iso((r: Raw) => (r.b, r.c, r.i, r.l, r.f, r.d))((Raw.apply _)tupled)

  implicit val rawEq: Equal[Raw] = Equal.equalA
  implicit val rawArb: Arbitrary[Raw] = Arbitrary(^^^^^(
    Arbitrary.arbitrary[Boolean],
    Arbitrary.arbitrary[Char],
    Arbitrary.arbitrary[Int],
    Arbitrary.arbitrary[Long],
    Arbitrary.arbitrary[Float],
    Arbitrary.arbitrary[Double])(Raw.apply))
}