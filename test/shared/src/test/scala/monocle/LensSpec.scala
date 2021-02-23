package monocle

import cats.Eq
import cats.arrow.{Category, Choice, Compose}
import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}
import monocle.law.discipline.{LensTests, OptionalTests, SetterTests, TraversalTests}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._

import scala.collection.immutable

case class Point(x: Int, y: Int)
case class Example(s: String, p: Point)

class LensSpec extends MonocleSuite {
  val s = Lens[Example, String](_.s)(s => ex => ex.copy(s = s))
  val p = Lens[Example, Point](_.p)(p => ex => ex.copy(p = p))

  val x  = Lens[Point, Int](_.x)(x => p => p.copy(x = x))
  val y  = Lens[Point, Int](_.y)(y => p => p.copy(y = y))
  val xy = Lens[Point, (Int, Int)](p => (p.x, p.y))(xy => p => p.copy(x = xy._1, y = xy._2))

  implicit val exampleGen: Arbitrary[Example] = Arbitrary(for {
    s <- arbitrary[String]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
  } yield Example(s, Point(x, y)))

  implicit val exampleEq: Eq[Example] = Eq.fromUniversalEquals[Example]

  checkAll("apply Lens", LensTests(s))

  checkAll("lens.asOptional", OptionalTests(s.asOptional))
  checkAll("lens.asTraversal", TraversalTests(s.asTraversal))
  checkAll("lens.asSetter", SetterTests(s.asSetter))

  checkAll("first", LensTests(s.first[Boolean]))
  checkAll("second", LensTests(s.second[Boolean]))

  // test implicit resolution of type classes

  test("Lens has a Compose instance") {
    assertEquals(Compose[Lens].compose(x, p).get(Example("plop", Point(3, 4))), 3)
  }

  test("Lens has a Category instance") {
    assertEquals(Category[Lens].id[Int].get(3), 3)
  }

  test("Lens has a Choice instance") {
    assertEquals(Choice[Lens].choice(x, y).get(Right(Point(5, 6))), 6)
  }

  test("get") {
    assertEquals(x.get(Point(5, 2)), 5)
  }

  test("find") {
    assertEquals(x.find(_ > 5)(Point(9, 2)), Some(9))
    assertEquals(x.find(_ > 5)(Point(3, 2)), None)
  }

  test("exist") {
    assert(x.exist(_ > 5)(Point(9, 2)))
    assert(!x.exist(_ > 5)(Point(3, 2)))
  }

  test("set") {
    assertEquals(x.replace(5)(Point(9, 2)), Point(5, 2))
  }

  test("modify") {
    assertEquals(x.modify(_ + 1)(Point(9, 2)), Point(10, 2))
  }

  test("to") {
    assertEquals(x.to(_.toString()).get(Point(1, 2)), "1")
  }

  test("some") {
    case class SomeTest(x: Int, y: Option[Int])
    val obj = SomeTest(1, Some(2))

    val lens = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue))

    assertEquals(lens.some.getOption(obj), Some(2))
    assertEquals(obj.optics.andThen(lens).some.getOption, Some(2))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val lens = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue))

    assertEquals(lens.withDefault(0).get(objSome), 2)
    assertEquals(lens.withDefault(0).get(objNone), 0)

    assertEquals(objNone.optics.andThen(lens).withDefault(0).get, 0)
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val lens = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue))

    assertEquals(lens.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.optics.andThen(lens).each.getAll, List(1, 2, 3))
  }

  test("filter") {
    case class SomeTest(x: Int, y: Int)
    val obj = SomeTest(1, 2)

    val lens = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue))

    assertEquals(lens.filter(_ > 0).getOption(obj), Some(2))
    assertEquals(obj.optics.andThen(lens).filter(_ > 0).getOption, Some(2))
  }

  test("filterIndex") {
    case class SomeTest(x: Int, y: List[String])
    val obj = SomeTest(1, List("hello", "world"))

    val lens = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue))

    assertEquals(lens.filterIndex((_: Int) > 0).getAll(obj), List("world"))
    assertEquals(obj.optics.andThen(lens).filterIndex((_: Int) > 0).getAll, List("world"))
  }

  test("at") {
    val tuple2     = (1, 2)
    val tuple2Lens = Iso.id[(Int, Int)].asLens
    assertEquals(tuple2Lens.at(1).get(tuple2), 1)
    assertEquals(tuple2Lens.at(2).get(tuple2), 2)
    assertEquals(tuple2.optics.andThen(tuple2Lens).at(1).get, 1)
    assertEquals(tuple2.optics.andThen(tuple2Lens).at(2).get, 2)

    val tuple3     = (1, 2, 3)
    val tuple3Lens = Iso.id[(Int, Int, Int)].asLens
    assertEquals(tuple3Lens.at(1).get(tuple3), 1)
    assertEquals(tuple3Lens.at(2).get(tuple3), 2)
    assertEquals(tuple3Lens.at(3).get(tuple3), 3)
    assertEquals(tuple3.optics.andThen(tuple3Lens).at(1).get, 1)
    assertEquals(tuple3.optics.andThen(tuple3Lens).at(2).get, 2)
    assertEquals(tuple3.optics.andThen(tuple3Lens).at(3).get, 3)

    val tuple4     = (1, 2, 3, 4)
    val tuple4Lens = Iso.id[(Int, Int, Int, Int)].asLens
    assertEquals(tuple4Lens.at(1).get(tuple4), 1)
    assertEquals(tuple4Lens.at(2).get(tuple4), 2)
    assertEquals(tuple4Lens.at(3).get(tuple4), 3)
    assertEquals(tuple4Lens.at(4).get(tuple4), 4)
    assertEquals(tuple4.optics.andThen(tuple4Lens).at(1).get, 1)
    assertEquals(tuple4.optics.andThen(tuple4Lens).at(2).get, 2)
    assertEquals(tuple4.optics.andThen(tuple4Lens).at(3).get, 3)
    assertEquals(tuple4.optics.andThen(tuple4Lens).at(4).get, 4)

    val tuple5     = (1, 2, 3, 4, 5)
    val tuple5Lens = Iso.id[(Int, Int, Int, Int, Int)].asLens
    assertEquals(tuple5Lens.at(1).get(tuple5), 1)
    assertEquals(tuple5Lens.at(2).get(tuple5), 2)
    assertEquals(tuple5Lens.at(3).get(tuple5), 3)
    assertEquals(tuple5Lens.at(4).get(tuple5), 4)
    assertEquals(tuple5Lens.at(5).get(tuple5), 5)
    assertEquals(tuple5.optics.andThen(tuple5Lens).at(1).get, 1)
    assertEquals(tuple5.optics.andThen(tuple5Lens).at(2).get, 2)
    assertEquals(tuple5.optics.andThen(tuple5Lens).at(3).get, 3)
    assertEquals(tuple5.optics.andThen(tuple5Lens).at(4).get, 4)
    assertEquals(tuple5.optics.andThen(tuple5Lens).at(5).get, 5)

    val tuple6     = (1, 2, 3, 4, 5, 6)
    val tuple6Lens = Iso.id[(Int, Int, Int, Int, Int, Int)].asLens
    assertEquals(tuple6Lens.at(1).get(tuple6), 1)
    assertEquals(tuple6Lens.at(2).get(tuple6), 2)
    assertEquals(tuple6Lens.at(3).get(tuple6), 3)
    assertEquals(tuple6Lens.at(4).get(tuple6), 4)
    assertEquals(tuple6Lens.at(5).get(tuple6), 5)
    assertEquals(tuple6Lens.at(6).get(tuple6), 6)
    assertEquals(tuple6.optics.andThen(tuple6Lens).at(1).get, 1)
    assertEquals(tuple6.optics.andThen(tuple6Lens).at(2).get, 2)
    assertEquals(tuple6.optics.andThen(tuple6Lens).at(3).get, 3)
    assertEquals(tuple6.optics.andThen(tuple6Lens).at(4).get, 4)
    assertEquals(tuple6.optics.andThen(tuple6Lens).at(5).get, 5)
    assertEquals(tuple6.optics.andThen(tuple6Lens).at(6).get, 6)

    val sortedMap     = immutable.SortedMap(1 -> "one")
    val sortedMapLens = Iso.id[immutable.SortedMap[Int, String]].asLens
    assertEquals(sortedMapLens.at(1).get(sortedMap), Some("one"))
    assertEquals(sortedMapLens.at(2).get(sortedMap), None)
    assertEquals(sortedMap.optics.andThen(sortedMapLens).at(1).get, Some("one"))
    assertEquals(sortedMap.optics.andThen(sortedMapLens).at(2).get, None)

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapLens = Iso.id[immutable.ListMap[Int, String]].asLens
    assertEquals(listMapLens.at(1).get(listMap), Some("one"))
    assertEquals(listMapLens.at(2).get(listMap), None)
    assertEquals(listMap.optics.andThen(listMapLens).at(1).get, Some("one"))
    assertEquals(listMap.optics.andThen(listMapLens).at(2).get, None)

    val map     = immutable.Map(1 -> "one")
    val mapLens = Iso.id[Map[Int, String]].asLens
    assertEquals(mapLens.at(1).get(map), Some("one"))
    assertEquals(mapLens.at(2).get(map), None)
    assertEquals(map.optics.andThen(mapLens).at(1).get, Some("one"))
    assertEquals(map.optics.andThen(mapLens).at(2).get, None)

    val set     = Set(1)
    val setLens = Iso.id[Set[Int]].asLens
    assertEquals(setLens.at(1).get(set), true)
    assertEquals(setLens.at(2).get(set), false)
    assertEquals(set.optics.andThen(setLens).at(1).get, true)
    assertEquals(set.optics.andThen(setLens).at(2).get, false)
  }

  test("index") {
    val list     = List(1)
    val listLens = Iso.id[List[Int]].asLens
    assertEquals(listLens.index(0).getOption(list), Some(1))
    assertEquals(listLens.index(1).getOption(list), None)
    assertEquals(list.optics.andThen(listLens).index(0).getOption, Some(1))
    assertEquals(list.optics.andThen(listLens).index(1).getOption, None)

    val lazyList     = LazyList(1)
    val lazyListLens = Iso.id[LazyList[Int]].asLens
    assertEquals(lazyListLens.index(0).getOption(lazyList), Some(1))
    assertEquals(lazyListLens.index(1).getOption(lazyList), None)
    assertEquals(lazyList.optics.andThen(lazyListLens).index(0).getOption, Some(1))
    assertEquals(lazyList.optics.andThen(lazyListLens).index(1).getOption, None)

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapLens = Iso.id[immutable.ListMap[Int, String]].asLens
    assertEquals(listMapLens.index(0).getOption(listMap), None)
    assertEquals(listMapLens.index(1).getOption(listMap), Some("one"))
    assertEquals(listMap.optics.andThen(listMapLens).index(0).getOption, None)
    assertEquals(listMap.optics.andThen(listMapLens).index(1).getOption, Some("one"))

    val map     = Map(1 -> "one")
    val mapLens = Iso.id[Map[Int, String]].asLens
    assertEquals(mapLens.index(1).getOption(map), Some("one"))
    assertEquals(mapLens.index(0).getOption(map), None)
    assertEquals(map.optics.andThen(mapLens).index(1).getOption, Some("one"))
    assertEquals(map.optics.andThen(mapLens).index(0).getOption, None)

    val sortedMap     = immutable.SortedMap(1 -> "one")
    val sortedMapLens = Iso.id[immutable.SortedMap[Int, String]].asLens
    assertEquals(sortedMapLens.index(1).getOption(sortedMap), Some("one"))
    assertEquals(sortedMapLens.index(0).getOption(sortedMap), None)
    assertEquals(sortedMap.optics.andThen(sortedMapLens).index(1).getOption, Some("one"))
    assertEquals(sortedMap.optics.andThen(sortedMapLens).index(0).getOption, None)

    val vector     = Vector(1)
    val vectorLens = Iso.id[Vector[Int]].asLens
    assertEquals(vectorLens.index(0).getOption(vector), Some(1))
    assertEquals(vectorLens.index(1).getOption(vector), None)
    assertEquals(vector.optics.andThen(vectorLens).index(0).getOption, Some(1))
    assertEquals(vector.optics.andThen(vectorLens).index(1).getOption, None)

    val chain     = Chain.one(1)
    val chainLens = Iso.id[Chain[Int]].asLens
    assertEquals(chainLens.index(0).getOption(chain), Some(1))
    assertEquals(chainLens.index(1).getOption(chain), None)
    assertEquals(chain.optics.andThen(chainLens).index(0).getOption, Some(1))
    assertEquals(chain.optics.andThen(chainLens).index(1).getOption, None)

    val nec     = NonEmptyChain.one(1)
    val necLens = Iso.id[NonEmptyChain[Int]].asLens
    assertEquals(necLens.index(0).getOption(nec), Some(1))
    assertEquals(necLens.index(1).getOption(nec), None)
    assertEquals(nec.optics.andThen(necLens).index(0).getOption, Some(1))
    assertEquals(nec.optics.andThen(necLens).index(1).getOption, None)

    val nev     = NonEmptyVector.one(1)
    val nevLens = Iso.id[NonEmptyVector[Int]].asLens
    assertEquals(nevLens.index(0).getOption(nev), Some(1))
    assertEquals(nevLens.index(1).getOption(nev), None)
    assertEquals(nev.optics.andThen(nevLens).index(0).getOption, Some(1))
    assertEquals(nev.optics.andThen(nevLens).index(1).getOption, None)

    val nel     = NonEmptyList.one(1)
    val nelLens = Iso.id[NonEmptyList[Int]].asLens
    assertEquals(nelLens.index(0).getOption(nel), Some(1))
    assertEquals(nelLens.index(1).getOption(nel), None)
    assertEquals(nel.optics.andThen(nelLens).index(0).getOption, Some(1))
    assertEquals(nel.optics.andThen(nelLens).index(1).getOption, None)
  }
}
