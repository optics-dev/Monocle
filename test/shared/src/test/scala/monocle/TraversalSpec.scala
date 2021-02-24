package monocle

import monocle.law.discipline.{SetterTests, TraversalTests}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import cats.Eq
import cats.arrow.{Category, Choice, Compose}
import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}
import cats.syntax.either._

import scala.collection.immutable

class TraversalSpec extends MonocleSuite {
  case class Location(latitude: Int, longitude: Int, name: String)

  val coordinates: Traversal[Location, Int] =
    Traversal.apply2[Location, Int](_.latitude, _.longitude) { case (newLat, newLong, oldLoc) =>
      oldLoc.copy(latitude = newLat, longitude = newLong)
    }

  def eachL[A]: Traversal[List[A], A]               = PTraversal.fromTraverse[List, A, A]
  val eachLi: Traversal[List[Int], Int]             = eachL[Int]
  def eachL2[A, B]: Traversal[List[(A, B)], (A, B)] = eachL[(A, B)]

  implicit val locationGen: Arbitrary[Location] = Arbitrary(for {
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    n <- arbitrary[String]
  } yield Location(x, y, n))

  implicit val exampleEq: Eq[Location] = Eq.fromUniversalEquals[Location]

  // Below we test a 7-lenses Traversal created using applyN

  // the test object
  case class ManyPropObject(p1: Int, p2: Int, p3: String, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int)

  // the 7 lenses for each int properties of the test object
  val l1: Lens[ManyPropObject, Int] = Lens((_: ManyPropObject).p1)(newValue => _.copy(p1 = newValue))
  val l2: Lens[ManyPropObject, Int] = Lens((_: ManyPropObject).p2)(newValue => _.copy(p2 = newValue))
  val l3: Lens[ManyPropObject, Int] = Lens((_: ManyPropObject).p4)(newValue => _.copy(p4 = newValue))
  val l4: Lens[ManyPropObject, Int] = Lens((_: ManyPropObject).p5)(newValue => _.copy(p5 = newValue))
  val l5: Lens[ManyPropObject, Int] = Lens((_: ManyPropObject).p6)(newValue => _.copy(p6 = newValue))
  val l6: Lens[ManyPropObject, Int] = Lens((_: ManyPropObject).p7)(newValue => _.copy(p7 = newValue))
  val l7: Lens[ManyPropObject, Int] = Lens((_: ManyPropObject).p8)(newValue => _.copy(p8 = newValue))

  // the 7-lenses Traversal generated using applyN
  val traversalN: Traversal[ManyPropObject, Int] =
    Traversal.applyN(l1, l2, l3, l4, l5, l6, l7)

  // the stub for generating random test objects
  implicit val manyPropObjectGen: Arbitrary[ManyPropObject] = Arbitrary(for {
    p1 <- arbitrary[Int]
    p2 <- arbitrary[Int]
    p3 <- arbitrary[String]
    p4 <- arbitrary[Int]
    p5 <- arbitrary[Int]
    p6 <- arbitrary[Int]
    p7 <- arbitrary[Int]
    p8 <- arbitrary[Int]
  } yield ManyPropObject(p1, p2, p3, p4, p5, p6, p7, p8))

  implicit val eqForManyPropObject: Eq[ManyPropObject] = Eq.fromUniversalEquals[ManyPropObject]

  checkAll("apply2 Traversal", TraversalTests(coordinates))
  checkAll("applyN Traversal", TraversalTests(traversalN))
  checkAll("fromTraverse Traversal", TraversalTests(eachLi))

  checkAll("traversal.asSetter", SetterTests(coordinates.asSetter))

  // test implicit resolution of type classes

  test("Traversal has a Compose instance") {
    assertEquals(
      Compose[Traversal]
        .compose(coordinates, eachL[Location])
        .modify(_ + 1)(List(Location(1, 2, ""), Location(3, 4, ""))),
      List(Location(2, 3, ""), Location(4, 5, ""))
    )
  }

  test("Traversal has a Category instance") {
    assertEquals(Category[Traversal].id[Int].getAll(3), List(3))
  }

  test("Traversal has a Choice instance") {
    assertEquals(
      Choice[Traversal]
        .choice(eachL[Int], coordinates)
        .modify(_ + 1)(Left(List(1, 2, 3))),
      Left(List(2, 3, 4))
    )
  }

  test("foldMap") {
    assertEquals(eachLi.foldMap(_.toString)(List(1, 2, 3, 4, 5)), "12345")
  }

  test("getAll") {
    assertEquals(eachLi.getAll(List(1, 2, 3, 4)), List(1, 2, 3, 4))
  }

  test("headOption") {
    assertEquals(eachLi.headOption(List(1, 2, 3, 4)), Some(1))
  }

  test("lastOption") {
    assertEquals(eachLi.lastOption(List(1, 2, 3, 4)), Some(4))
  }

  test("length") {
    assertEquals(eachLi.length(List(1, 2, 3, 4)), 4)
    assertEquals(eachLi.length(Nil), 0)
  }

  test("isEmpty") {
    assertEquals(eachLi.isEmpty(List(1, 2, 3, 4)), false)
    assertEquals(eachLi.isEmpty(Nil), true)
  }

  test("nonEmpty") {
    assertEquals(eachLi.nonEmpty(List(1, 2, 3, 4)), true)
    assertEquals(eachLi.nonEmpty(Nil), false)
  }

  test("find") {
    assertEquals(eachLi.find(_ > 2)(List(1, 2, 3, 4)), Some(3))
    assertEquals(eachLi.find(_ > 9)(List(1, 2, 3, 4)), None)
  }

  test("exist") {
    assertEquals(eachLi.exist(_ > 2)(List(1, 2, 3, 4)), true)
    assertEquals(eachLi.exist(_ > 9)(List(1, 2, 3, 4)), false)
    assertEquals(eachLi.exist(_ > 9)(Nil), false)
  }

  test("all") {
    assertEquals(eachLi.all(_ > 2)(List(1, 2, 3, 4)), false)
    assertEquals(eachLi.all(_ > 0)(List(1, 2, 3, 4)), true)
    assertEquals(eachLi.all(_ > 0)(Nil), true)
  }

  test("set") {
    assertEquals(eachLi.replace(0)(List(1, 2, 3, 4)), List(0, 0, 0, 0))
  }

  test("modify") {
    assertEquals(eachLi.modify(_ + 1)(List(1, 2, 3, 4)), List(2, 3, 4, 5))
  }

  test("parModifyF") {
    assertEquals(
      eachLi.parModifyF[Either[Unit, *]](i => (i + 1).asRight[Unit])(List(1, 2, 3, 4)),
      Right(List(2, 3, 4, 5))
    )
    // `Left` values should be accumulated through `Validated`.
    assertEquals(eachLi.parModifyF[Either[String, *]](_.toString.asLeft[Int])(List(1, 2, 3, 4)), Left("1234"))
  }

  test("to") {
    assertEquals(eachLi.to(_.toString()).getAll(List(1, 2, 3)), List("1", "2", "3"))
  }

  test("some") {
    val numbers   = List(Some(1), None, Some(2), None)
    val traversal = Traversal.fromTraverse[List, Option[Int]]

    assertEquals(traversal.some.replace(5)(numbers), List(Some(5), None, Some(5), None))
    assertEquals(numbers.focus().andThen(traversal).some.replace(5), List(Some(5), None, Some(5), None))
  }

  test("withDefault") {
    val numbers   = List(Some(1), None, Some(2), None)
    val traversal = Traversal.fromTraverse[List, Option[Int]]

    assertEquals(traversal.withDefault(0).modify(_ + 1)(numbers), List(Some(2), Some(1), Some(3), Some(1)))
    assertEquals(
      numbers.focus().andThen(traversal).withDefault(0).modify(_ + 1),
      List(Some(2), Some(1), Some(3), Some(1))
    )
  }

  test("each") {
    val numbers   = List(List(1, 2, 3), Nil, List(4), Nil)
    val traversal = Traversal.fromTraverse[List, List[Int]]

    assertEquals(traversal.each.getAll(numbers), List(1, 2, 3, 4))
    assertEquals(numbers.focus().andThen(traversal).each.getAll, List(1, 2, 3, 4))
  }

  test("filter") {
    val numbers   = List(1, 2, 3)
    val traversal = Traversal.fromTraverse[List, Int]

    assertEquals(traversal.filter(_ > 1).getAll(numbers), List(2, 3))
    assertEquals(numbers.focus().andThen(traversal).filter(_ > 1).getAll, List(2, 3))
  }

  test("filterIndex") {
    val words     = List(List("hello", "world"), List("hey", "hi"))
    val traversal = Traversal.fromTraverse[List, List[String]]

    assertEquals(traversal.filterIndex((_: Int) > 0).getAll(words), List("world", "hi"))
    assertEquals(words.focus().andThen(traversal).filterIndex((_: Int) > 0).getAll, List("world", "hi"))
  }

  test("at") {
    val sortedMap          = immutable.SortedMap(1 -> "one")
    val sortedMapTraversal = Iso.id[immutable.SortedMap[Int, String]].asTraversal
    assertEquals(sortedMapTraversal.at(1).getAll(sortedMap), List(Some("one")))
    assertEquals(sortedMapTraversal.at(0).getAll(sortedMap), List(None))
    assertEquals(sortedMap.focus().andThen(sortedMapTraversal).at(1).getAll, List(Some("one")))
    assertEquals(sortedMap.focus().andThen(sortedMapTraversal).at(0).getAll, List(None))

    val listMap          = immutable.ListMap(1 -> "one")
    val listMapTraversal = Iso.id[immutable.ListMap[Int, String]].asTraversal
    assertEquals(listMapTraversal.at(1).getAll(listMap), List(Some("one")))
    assertEquals(listMapTraversal.at(0).getAll(listMap), List(None))
    assertEquals(listMap.focus().andThen(listMapTraversal).at(1).getAll, List(Some("one")))
    assertEquals(listMap.focus().andThen(listMapTraversal).at(0).getAll, List(None))

    val map          = immutable.Map(1 -> "one")
    val mapTraversal = Iso.id[Map[Int, String]].asTraversal
    assertEquals(mapTraversal.at(1).getAll(map), List(Some("one")))
    assertEquals(mapTraversal.at(0).getAll(map), List(None))
    assertEquals(map.focus().andThen(mapTraversal).at(1).getAll, List(Some("one")))
    assertEquals(map.focus().andThen(mapTraversal).at(0).getAll, List(None))

    val set          = Set(1)
    val setTraversal = Iso.id[Set[Int]].asTraversal
    assertEquals(setTraversal.at(1).getAll(set), List(true))
    assertEquals(setTraversal.at(0).getAll(set), List(false))
    assertEquals(set.focus().andThen(setTraversal).at(1).getAll, List(true))
    assertEquals(set.focus().andThen(setTraversal).at(0).getAll, List(false))
  }

  test("index") {
    val list          = List(1)
    val listTraversal = Iso.id[List[Int]].asTraversal
    assertEquals(listTraversal.index(0).getAll(list), List(1))
    assertEquals(listTraversal.index(1).getAll(list), Nil)
    assertEquals(list.focus().andThen(listTraversal).index(0).getAll, List(1))
    assertEquals(list.focus().andThen(listTraversal).index(1).getAll, Nil)

    val lazyList          = LazyList(1)
    val lazyListTraversal = Iso.id[LazyList[Int]].asTraversal
    assertEquals(lazyListTraversal.index(0).getAll(lazyList), List(1))
    assertEquals(lazyListTraversal.index(1).getAll(lazyList), Nil)
    assertEquals(lazyList.focus().andThen(lazyListTraversal).index(0).getAll, List(1))
    assertEquals(lazyList.focus().andThen(lazyListTraversal).index(1).getAll, Nil)

    val listMap          = immutable.ListMap(1 -> "one")
    val listMapTraversal = Iso.id[immutable.ListMap[Int, String]].asTraversal
    assertEquals(listMapTraversal.index(0).getAll(listMap), Nil)
    assertEquals(listMapTraversal.index(1).getAll(listMap), List("one"))
    assertEquals(listMap.focus().andThen(listMapTraversal).index(0).getAll, Nil)
    assertEquals(listMap.focus().andThen(listMapTraversal).index(1).getAll, List("one"))

    val map          = Map(1 -> "one")
    val mapTraversal = Iso.id[Map[Int, String]].asTraversal
    assertEquals(mapTraversal.index(0).getAll(map), Nil)
    assertEquals(mapTraversal.index(1).getAll(map), List("one"))
    assertEquals(map.focus().andThen(mapTraversal).index(0).getAll, Nil)
    assertEquals(map.focus().andThen(mapTraversal).index(1).getAll, List("one"))

    val sortedMap          = immutable.SortedMap(1 -> "one")
    val sortedMapTraversal = Iso.id[immutable.SortedMap[Int, String]].asTraversal
    assertEquals(sortedMapTraversal.index(0).getAll(sortedMap), Nil)
    assertEquals(sortedMapTraversal.index(1).getAll(sortedMap), List("one"))
    assertEquals(sortedMap.focus().andThen(sortedMapTraversal).index(0).getAll, Nil)
    assertEquals(sortedMap.focus().andThen(sortedMapTraversal).index(1).getAll, List("one"))

    val vector          = Vector(1)
    val vectorTraversal = Iso.id[Vector[Int]].asTraversal
    assertEquals(vectorTraversal.index(0).getAll(vector), List(1))
    assertEquals(vectorTraversal.index(1).getAll(vector), Nil)
    assertEquals(vector.focus().andThen(vectorTraversal).index(0).getAll, List(1))
    assertEquals(vector.focus().andThen(vectorTraversal).index(1).getAll, Nil)

    val chain          = Chain.one(1)
    val chainTraversal = Iso.id[Chain[Int]].asTraversal
    assertEquals(chainTraversal.index(0).getAll(chain), List(1))
    assertEquals(chainTraversal.index(1).getAll(chain), Nil)
    assertEquals(chain.focus().andThen(chainTraversal).index(0).getAll, List(1))
    assertEquals(chain.focus().andThen(chainTraversal).index(1).getAll, Nil)

    val nec          = NonEmptyChain.one(1)
    val necTraversal = Iso.id[NonEmptyChain[Int]].asTraversal
    assertEquals(necTraversal.index(0).getAll(nec), List(1))
    assertEquals(necTraversal.index(1).getAll(nec), Nil)
    assertEquals(nec.focus().andThen(necTraversal).index(0).getAll, List(1))
    assertEquals(nec.focus().andThen(necTraversal).index(1).getAll, Nil)

    val nev          = NonEmptyVector.one(1)
    val nevTraversal = Iso.id[NonEmptyVector[Int]].asTraversal
    assertEquals(nevTraversal.index(0).getAll(nev), List(1))
    assertEquals(nevTraversal.index(1).getAll(nev), Nil)
    assertEquals(nev.focus().andThen(nevTraversal).index(0).getAll, List(1))
    assertEquals(nev.focus().andThen(nevTraversal).index(1).getAll, Nil)

    val nel          = NonEmptyList.one(1)
    val nelTraversal = Iso.id[NonEmptyList[Int]].asTraversal
    assertEquals(nelTraversal.index(0).getAll(nel), List(1))
    assertEquals(nelTraversal.index(1).getAll(nel), Nil)
    assertEquals(nel.focus().andThen(nelTraversal).index(0).getAll, List(1))
    assertEquals(nel.focus().andThen(nelTraversal).index(1).getAll, Nil)
  }
}
