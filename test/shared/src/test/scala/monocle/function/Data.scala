package monocle.function

import monocle._
import org.scalacheck.{Cogen, Arbitrary}

import cats.{Eq => Equal, Order}
import cats.data.NonEmptyList
import cats.syntax.apply._

import scala.collection.immutable.SortedMap

case class MMap[K, V](map: SortedMap[K, V])

object MMap {
  def toSortedMap[K, V]: Iso[MMap[K, V], SortedMap[K, V]] =
    Iso[MMap[K, V], SortedMap[K, V]](_.map)(MMap(_))

  implicit def mmapEq[K, V]: Equal[MMap[K, V]] = Equal.fromUniversalEquals
  implicit def mmapArb[K: Arbitrary, V: Arbitrary](implicit ok: Order[K]): Arbitrary[MMap[K, V]] =
    Arbitrary(Arbitrary.arbitrary[List[(K, V)]].map(kvs => MMap(SortedMap(kvs: _*)(ok.toOrdering))))
}

case class CNel(head: Char, tail: List[Char])

object CNel extends TestInstances {
  val toNel: Iso[CNel, NonEmptyList[Char]] =
    Iso[CNel, NonEmptyList[Char]](c => NonEmptyList(c.head, c.tail))(n => CNel(n.head, n.tail))

  implicit val cNelEq: Equal[CNel] = Equal.fromUniversalEquals
  implicit val cNelArb: Arbitrary[CNel] = Arbitrary((Arbitrary.arbitrary[Char], Arbitrary.arbitrary[List[Char]]).mapN(CNel.apply))
}

case class CList(list: List[Char])

object CList {
  val toList: Iso[CList, List[Char]] = Iso[CList, List[Char]](_.list)(CList(_))

  implicit val clistEq: Equal[CList] = Equal.fromUniversalEquals
  implicit val clistArb: Arbitrary[CList] = Arbitrary(Arbitrary.arbitrary[List[Char]].map(CList(_)))
  implicit val clistCoGen: Cogen[CList] = Cogen.cogenList[Char].contramap[CList](_.list)
}

case class Raw(b: Boolean, c: Char, i: Int, l: Long, f: Float, d: Double)

object Raw extends TestInstances {
  val toTuple: Iso[Raw, (Boolean, Char, Int, Long, Float, Double)] =
    Iso((r: Raw) => (r.b, r.c, r.i, r.l, r.f, r.d))((Raw.apply _)tupled)

  implicit val rawEq: Equal[Raw] = Equal.fromUniversalEquals
  implicit val rawArb: Arbitrary[Raw] = Arbitrary((
    Arbitrary.arbitrary[Boolean],
    Arbitrary.arbitrary[Char],
    Arbitrary.arbitrary[Int],
    Arbitrary.arbitrary[Long],
    Arbitrary.arbitrary[Float],
    Arbitrary.arbitrary[Double]).mapN(Raw.apply))
}
