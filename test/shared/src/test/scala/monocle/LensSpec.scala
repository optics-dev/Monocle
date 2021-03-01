package monocle

import cats.{Eq, Semigroupal}
import cats.arrow.{Category, Choice, Compose}
import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}
import cats.implicits.catsSyntaxTuple2Semigroupal
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

  implicit val arbitraryPointEq: Eq[Point] = Eq.fromUniversalEquals

  implicit val arbitraryPoint: Arbitrary[Point] = Arbitrary(
    for {
      x <- arbitrary[Int]
      y <- arbitrary[Int]
    } yield Point(x, y)
  )
  implicit val exampleGen: Arbitrary[Example] = Arbitrary(for {
    s <- arbitrary[String]
    p <- arbitrary[Point]
  } yield Example(s, p))

  implicit val exampleEq: Eq[Example] = Eq.fromUniversalEquals[Example]

  checkAll("apply Lens", LensTests(s))

  checkAll("lens.asOptional", OptionalTests(s.asOptional))
  checkAll("lens.asTraversal", TraversalTests(s.asTraversal))
  checkAll("lens.asSetter", SetterTests(s.asSetter))

  checkAll("first", LensTests(s.first[Boolean]))
  checkAll("second", LensTests(s.second[Boolean]))

  //Conflicting lenses break the get what you replace law
  //checkAll("tupled", LensTests((x,x).tupled))
  checkAll("tupled", LensTests((x,y).tupled))


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

  test("Lens has a Semigroupal instance") {
    assertEquals(Semigroupal[Lens[Point, *]].product(x, y).get(Point(5, 6)), (5, 6))
  }

  test("Lens has an Invariant instance") {
    val zippedLenses: Lens[Point, (Int, Int)] = (x, y).tupled
    assertEquals(
      zippedLenses.replace((2, 3))(Point(5, 6)),
      Point(2, 3)
    )
  }

  test("tupled breaks get what you replace if you tuple the same/conflicting lenses") {
    val zippedLenses: Lens[Point, (Int, Int)] = (x, x).tupled
    assertEquals(
      zippedLenses.replace((2, 3))(Point(5, 6)),
      Point(3, 6)
    ) //2 disappeared

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
    assertEquals(obj.focus().andThen(lens).some.getOption, Some(2))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val lens = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue))

    assertEquals(lens.withDefault(0).get(objSome), 2)
    assertEquals(lens.withDefault(0).get(objNone), 0)

    assertEquals(objNone.focus().andThen(lens).withDefault(0).get, 0)
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val lens = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue))

    assertEquals(lens.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.focus().andThen(lens).each.getAll, List(1, 2, 3))
  }

  test("filter") {
    case class SomeTest(x: Int, y: Int)
    val obj = SomeTest(1, 2)

    val lens = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue))

    assertEquals(lens.filter(_ > 0).getOption(obj), Some(2))
    assertEquals(obj.focus().andThen(lens).filter(_ > 0).getOption, Some(2))
  }

  test("filterIndex") {
    case class SomeTest(x: Int, y: List[String])
    val obj = SomeTest(1, List("hello", "world"))

    val lens = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue))

    assertEquals(lens.filterIndex((_: Int) > 0).getAll(obj), List("world"))
    assertEquals(obj.focus().andThen(lens).filterIndex((_: Int) > 0).getAll, List("world"))
  }

  test("at") {
    val sortedMap     = immutable.SortedMap(1 -> "one")
    val sortedMapLens = Iso.id[immutable.SortedMap[Int, String]].asLens
    assertEquals(sortedMapLens.at(1).get(sortedMap), Some("one"))
    assertEquals(sortedMapLens.at(2).get(sortedMap), None)
    assertEquals(sortedMap.focus().andThen(sortedMapLens).at(1).get, Some("one"))
    assertEquals(sortedMap.focus().andThen(sortedMapLens).at(2).get, None)

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapLens = Iso.id[immutable.ListMap[Int, String]].asLens
    assertEquals(listMapLens.at(1).get(listMap), Some("one"))
    assertEquals(listMapLens.at(2).get(listMap), None)
    assertEquals(listMap.focus().andThen(listMapLens).at(1).get, Some("one"))
    assertEquals(listMap.focus().andThen(listMapLens).at(2).get, None)

    val map     = immutable.Map(1 -> "one")
    val mapLens = Iso.id[Map[Int, String]].asLens
    assertEquals(mapLens.at(1).get(map), Some("one"))
    assertEquals(mapLens.at(2).get(map), None)
    assertEquals(map.focus().andThen(mapLens).at(1).get, Some("one"))
    assertEquals(map.focus().andThen(mapLens).at(2).get, None)

    val set     = Set(1)
    val setLens = Iso.id[Set[Int]].asLens
    assertEquals(setLens.at(1).get(set), true)
    assertEquals(setLens.at(2).get(set), false)
    assertEquals(set.focus().andThen(setLens).at(1).get, true)
    assertEquals(set.focus().andThen(setLens).at(2).get, false)
  }

  test("index") {
    val list     = List(1)
    val listLens = Iso.id[List[Int]].asLens
    assertEquals(listLens.index(0).getOption(list), Some(1))
    assertEquals(listLens.index(1).getOption(list), None)
    assertEquals(list.focus().andThen(listLens).index(0).getOption, Some(1))
    assertEquals(list.focus().andThen(listLens).index(1).getOption, None)

    val lazyList     = LazyList(1)
    val lazyListLens = Iso.id[LazyList[Int]].asLens
    assertEquals(lazyListLens.index(0).getOption(lazyList), Some(1))
    assertEquals(lazyListLens.index(1).getOption(lazyList), None)
    assertEquals(lazyList.focus().andThen(lazyListLens).index(0).getOption, Some(1))
    assertEquals(lazyList.focus().andThen(lazyListLens).index(1).getOption, None)

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapLens = Iso.id[immutable.ListMap[Int, String]].asLens
    assertEquals(listMapLens.index(0).getOption(listMap), None)
    assertEquals(listMapLens.index(1).getOption(listMap), Some("one"))
    assertEquals(listMap.focus().andThen(listMapLens).index(0).getOption, None)
    assertEquals(listMap.focus().andThen(listMapLens).index(1).getOption, Some("one"))

    val map     = Map(1 -> "one")
    val mapLens = Iso.id[Map[Int, String]].asLens
    assertEquals(mapLens.index(1).getOption(map), Some("one"))
    assertEquals(mapLens.index(0).getOption(map), None)
    assertEquals(map.focus().andThen(mapLens).index(1).getOption, Some("one"))
    assertEquals(map.focus().andThen(mapLens).index(0).getOption, None)

    val sortedMap     = immutable.SortedMap(1 -> "one")
    val sortedMapLens = Iso.id[immutable.SortedMap[Int, String]].asLens
    assertEquals(sortedMapLens.index(1).getOption(sortedMap), Some("one"))
    assertEquals(sortedMapLens.index(0).getOption(sortedMap), None)
    assertEquals(sortedMap.focus().andThen(sortedMapLens).index(1).getOption, Some("one"))
    assertEquals(sortedMap.focus().andThen(sortedMapLens).index(0).getOption, None)

    val vector     = Vector(1)
    val vectorLens = Iso.id[Vector[Int]].asLens
    assertEquals(vectorLens.index(0).getOption(vector), Some(1))
    assertEquals(vectorLens.index(1).getOption(vector), None)
    assertEquals(vector.focus().andThen(vectorLens).index(0).getOption, Some(1))
    assertEquals(vector.focus().andThen(vectorLens).index(1).getOption, None)

    val chain     = Chain.one(1)
    val chainLens = Iso.id[Chain[Int]].asLens
    assertEquals(chainLens.index(0).getOption(chain), Some(1))
    assertEquals(chainLens.index(1).getOption(chain), None)
    assertEquals(chain.focus().andThen(chainLens).index(0).getOption, Some(1))
    assertEquals(chain.focus().andThen(chainLens).index(1).getOption, None)

    val nec     = NonEmptyChain.one(1)
    val necLens = Iso.id[NonEmptyChain[Int]].asLens
    assertEquals(necLens.index(0).getOption(nec), Some(1))
    assertEquals(necLens.index(1).getOption(nec), None)
    assertEquals(nec.focus().andThen(necLens).index(0).getOption, Some(1))
    assertEquals(nec.focus().andThen(necLens).index(1).getOption, None)

    val nev     = NonEmptyVector.one(1)
    val nevLens = Iso.id[NonEmptyVector[Int]].asLens
    assertEquals(nevLens.index(0).getOption(nev), Some(1))
    assertEquals(nevLens.index(1).getOption(nev), None)
    assertEquals(nev.focus().andThen(nevLens).index(0).getOption, Some(1))
    assertEquals(nev.focus().andThen(nevLens).index(1).getOption, None)

    val nel     = NonEmptyList.one(1)
    val nelLens = Iso.id[NonEmptyList[Int]].asLens
    assertEquals(nelLens.index(0).getOption(nel), Some(1))
    assertEquals(nelLens.index(1).getOption(nel), None)
    assertEquals(nel.focus().andThen(nelLens).index(0).getOption, Some(1))
    assertEquals(nel.focus().andThen(nelLens).index(1).getOption, None)
  }
}
