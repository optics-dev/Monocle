package monocle.function

import monocle._
import org.scalacheck.{Arbitrary, Cogen}
import cats.{Eq, Order}
import cats.data.{NonEmptyList, NonEmptyVector}
import cats.syntax.apply._

import scala.collection.immutable.SortedMap

case class MSorteMap[K, V](map: SortedMap[K, V])

object MSorteMap {
  def toSortedMap[K, V]: Iso[MSorteMap[K, V], SortedMap[K, V]] =
    Iso[MSorteMap[K, V], SortedMap[K, V]](_.map)(MSorteMap(_))

  implicit def mmapEq[K, V]: Eq[MSorteMap[K, V]] = Eq.fromUniversalEquals
  implicit def mmapArb[K: Arbitrary, V: Arbitrary](implicit ok: Order[K]): Arbitrary[MSorteMap[K, V]] =
    Arbitrary(Arbitrary.arbitrary[List[(K, V)]].map(kvs => MSorteMap(SortedMap(kvs: _*)(ok.toOrdering))))
}

case class MMap[K, V](map: Map[K, V])

object MMap {
  def toMap[K, V]: Iso[MMap[K, V], Map[K, V]] =
    Iso[MMap[K, V], Map[K, V]](_.map)(MMap(_))

  implicit def mmapEq[K, V]: Eq[MMap[K, V]] = Eq.fromUniversalEquals
  implicit def mmapArb[K: Arbitrary, V: Arbitrary]: Arbitrary[MMap[K, V]] =
    Arbitrary(Arbitrary.arbitrary[List[(K, V)]].map(kvs => MMap(Map(kvs: _*))))
}

case class CNel(head: Char, tail: List[Char])

object CNel extends TestInstances {
  val toNel: Iso[CNel, NonEmptyList[Char]] =
    Iso[CNel, NonEmptyList[Char]](c => NonEmptyList(c.head, c.tail))(n => CNel(n.head, n.tail))

  implicit val cNelEq: Eq[CNel] = Eq.fromUniversalEquals
  implicit val cNelArb: Arbitrary[CNel] = Arbitrary(
    (Arbitrary.arbitrary[Char], Arbitrary.arbitrary[List[Char]]).mapN(CNel.apply)
  )
}

case class CNev(head: Char, tail: Vector[Char])

object CNev extends TestInstances {
  val toNev: Iso[CNev, NonEmptyVector[Char]] =
    Iso[CNev, NonEmptyVector[Char]](c => NonEmptyVector(c.head, c.tail))(n => CNev(n.head, n.tail))

  implicit val cNevEq: Eq[CNev] = Eq.fromUniversalEquals
  implicit val cNevArb: Arbitrary[CNev] = Arbitrary(
    (Arbitrary.arbitrary[Char], Arbitrary.arbitrary[Vector[Char]]).mapN(CNev.apply)
  )
}

case class CList(list: List[Char])

object CList {
  val toList: Iso[CList, List[Char]] = Iso[CList, List[Char]](_.list)(CList(_))

  implicit val clistEq: Eq[CList]         = Eq.fromUniversalEquals
  implicit val clistArb: Arbitrary[CList] = Arbitrary(Arbitrary.arbitrary[List[Char]].map(CList(_)))
  implicit val clistCoGen: Cogen[CList]   = Cogen.cogenList[Char].contramap[CList](_.list)
}

case class Raw(b: Boolean, c: Char, i: Int, l: Long, f: Float, d: Double)

object Raw extends TestInstances {
  val toTuple: Iso[Raw, (Boolean, Char, Int, Long, Float, Double)] =
    Iso((r: Raw) => (r.b, r.c, r.i, r.l, r.f, r.d))((Raw.apply _) tupled)

  implicit val rawEq: Eq[Raw] = Eq.fromUniversalEquals
  implicit val rawArb: Arbitrary[Raw] = Arbitrary(
    (
      Arbitrary.arbitrary[Boolean],
      Arbitrary.arbitrary[Char],
      Arbitrary.arbitrary[Int],
      Arbitrary.arbitrary[Long],
      Arbitrary.arbitrary[Float],
      Arbitrary.arbitrary[Double]
    ).mapN(Raw.apply)
  )
}
