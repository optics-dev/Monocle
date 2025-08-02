package monocle

import cats.Semigroupal
import cats.arrow.{Arrow, Category, Choice, Compose, Profunctor}
import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}

import scala.collection.immutable

class GetterSpec extends MonocleSuite {
  case class Bar(i: Int)
  case class Foo(bar: Bar)

  val bar = Getter[Foo, Bar](_.bar)
  val i   = Getter[Bar, Int](_.i)

  // test implicit resolution of type classes

  test("Getter has a Compose instance") {
    assertEquals(Compose[Getter].compose(i, bar).get(Foo(Bar(3))), 3)
  }

  test("Getter has a Category instance") {
    assertEquals(Category[Getter].id[Int].get(3), 3)
  }

  test("Getter has a Choice instance") {
    assertEquals(
      Choice[Getter]
        .choice(i, Choice[Getter].id[Int])
        .get(Left(Bar(3))),
      3
    )
  }

  test("Getter has a Profunctor instance") {
    assertEquals(Profunctor[Getter].rmap(bar)(_.i).get(Foo(Bar(3))), 3)
  }

  test("Getter has a Arrow instance") {
    assertEquals(Arrow[Getter].lift((_: Int) * 2).get(4), 8)
  }

  test("Getter has a Semigroupal instance") {
    val length = Getter[String, Int](_.length)
    val upper  = Getter[String, String](_.toUpperCase)
    assertEquals(
      Semigroupal[Getter[String, *]]
        .product(length, upper)
        .get("helloworld"),
      (10, "HELLOWORLD")
    )
  }

  test("get") {
    assertEquals(i.get(Bar(5)), 5)
  }

  test("find") {
    assertEquals(i.find(_ > 5)(Bar(9)), Some(9))
    assertEquals(i.find(_ > 5)(Bar(3)), None)
  }

  test("exist") {
    assertEquals(i.exist(_ > 5)(Bar(9)), true)
    assertEquals(i.exist(_ > 5)(Bar(3)), false)
  }

  test("zip") {
    val length = Getter[String, Int](_.length)
    val upper  = Getter[String, String](_.toUpperCase)
    assertEquals(length.zip(upper).get("helloworld"), (10, "HELLOWORLD"))
  }

  test("to") {
    assertEquals(i.to(_.toString()).get(Bar(5)), "5")
  }

  test("some") {
    case class SomeTest(x: Int, y: Option[Int])
    val obj = SomeTest(1, Some(2))

    val getter = Getter((_: SomeTest).y)

    assertEquals(getter.some.getAll(obj), List(2))
    assertEquals(obj.focus().andThen(getter).some.getAll, List(2))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val getter = Getter((_: SomeTest).y)

    assertEquals(getter.withDefault(0).get(objSome), 2)
    assertEquals(getter.withDefault(0).get(objNone), 0)

    assertEquals(objSome.focus().andThen(getter).withDefault(0).get, 2)
    assertEquals(objNone.focus().andThen(getter).withDefault(0).get, 0)
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val getter = Getter((_: SomeTest).y)

    assertEquals(getter.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.focus().andThen(getter).each.getAll, List(1, 2, 3))
  }

  test("filter") {
    case class SomeTest(x: Int, y: Int)
    val obj = SomeTest(1, 2)

    val getter = Getter[SomeTest, Int](_.y)

    assertEquals(getter.filter(_ > 0).getAll(obj), List(2))
    assertEquals(obj.focus().andThen(getter).filter(_ > 0).getAll, List(2))
  }

  test("filterIndex") {
    case class SomeTest(x: Int, y: List[String])
    val obj = SomeTest(1, List("hello", "world"))

    val getter = Getter[SomeTest, List[String]](_.y)

    assertEquals(getter.filterIndex((_: Int) > 0).getAll(obj), List("world"))
    assertEquals(obj.focus().andThen(getter).filterIndex((_: Int) > 0).getAll, List("world"))
  }

  test("at") {
    val sortedMap       = immutable.SortedMap(1 -> "one")
    val sortedMapGetter = Iso.id[immutable.SortedMap[Int, String]].asGetter
    assertEquals(sortedMapGetter.at(1).get(sortedMap), Some("one"))
    assertEquals(sortedMapGetter.at(0).get(sortedMap), None)
    assertEquals(sortedMap.focus().andThen(sortedMapGetter).at(1).get, Some("one"))
    assertEquals(sortedMap.focus().andThen(sortedMapGetter).at(0).get, None)

    val listMap       = immutable.ListMap(1 -> "one")
    val listMapGetter = Iso.id[immutable.ListMap[Int, String]].asGetter
    assertEquals(listMapGetter.at(1).get(listMap), Some("one"))
    assertEquals(listMapGetter.at(0).get(listMap), None)
    assertEquals(listMap.focus().andThen(listMapGetter).at(1).get, Some("one"))
    assertEquals(listMap.focus().andThen(listMapGetter).at(0).get, None)

    val map       = immutable.Map(1 -> "one")
    val mapGetter = Iso.id[Map[Int, String]].asGetter
    assertEquals(mapGetter.at(1).get(map), Some("one"))
    assertEquals(mapGetter.at(0).get(map), None)
    assertEquals(map.focus().andThen(mapGetter).at(1).get, Some("one"))
    assertEquals(map.focus().andThen(mapGetter).at(0).get, None)

    val set       = Set(1)
    val setGetter = Iso.id[Set[Int]].asGetter
    assertEquals(setGetter.at(1).get(set), true)
    assertEquals(setGetter.at(0).get(set), false)
    assertEquals(set.focus().andThen(setGetter).at(1).get, true)
    assertEquals(set.focus().andThen(setGetter).at(0).get, false)
  }

  test("index") {
    val list       = List(1)
    val listGetter = Iso.id[List[Int]].asGetter
    assertEquals(listGetter.index(0).getAll(list), List(1))
    assertEquals(listGetter.index(1).getAll(list), Nil)
    assertEquals(list.focus().andThen(listGetter).index(0).getAll, List(1))
    assertEquals(list.focus().andThen(listGetter).index(1).getAll, Nil)

    val lazyList       = LazyList(1)
    val lazyListGetter = Iso.id[LazyList[Int]].asGetter
    assertEquals(lazyListGetter.index(0).getAll(lazyList), List(1))
    assertEquals(lazyListGetter.index(1).getAll(lazyList), Nil)
    assertEquals(lazyList.focus().andThen(lazyListGetter).index(0).getAll, List(1))
    assertEquals(lazyList.focus().andThen(lazyListGetter).index(1).getAll, Nil)

    val listMap       = immutable.ListMap(1 -> "one")
    val listMapGetter = Iso.id[immutable.ListMap[Int, String]].asGetter
    assertEquals(listMapGetter.index(0).getAll(listMap), Nil)
    assertEquals(listMapGetter.index(1).getAll(listMap), List("one"))
    assertEquals(listMap.focus().andThen(listMapGetter).index(0).getAll, Nil)
    assertEquals(listMap.focus().andThen(listMapGetter).index(1).getAll, List("one"))

    val map       = Map(1 -> "one")
    val mapGetter = Iso.id[Map[Int, String]].asGetter
    assertEquals(mapGetter.index(0).getAll(map), Nil)
    assertEquals(mapGetter.index(1).getAll(map), List("one"))
    assertEquals(map.focus().andThen(mapGetter).index(0).getAll, Nil)
    assertEquals(map.focus().andThen(mapGetter).index(1).getAll, List("one"))

    val sortedMap       = immutable.SortedMap(1 -> "one")
    val sortedMapGetter = Iso.id[immutable.SortedMap[Int, String]].asGetter
    assertEquals(sortedMapGetter.index(0).getAll(sortedMap), Nil)
    assertEquals(sortedMapGetter.index(1).getAll(sortedMap), List("one"))
    assertEquals(sortedMap.focus().andThen(sortedMapGetter).index(0).getAll, Nil)
    assertEquals(sortedMap.focus().andThen(sortedMapGetter).index(1).getAll, List("one"))

    val vector       = Vector(1)
    val vectorGetter = Iso.id[Vector[Int]].asGetter
    assertEquals(vectorGetter.index(0).getAll(vector), List(1))
    assertEquals(vectorGetter.index(1).getAll(vector), Nil)
    assertEquals(vector.focus().andThen(vectorGetter).index(0).getAll, List(1))
    assertEquals(vector.focus().andThen(vectorGetter).index(1).getAll, Nil)

    val chain       = Chain.one(1)
    val chainGetter = Iso.id[Chain[Int]].asGetter
    assertEquals(chainGetter.index(0).getAll(chain), List(1))
    assertEquals(chainGetter.index(1).getAll(chain), Nil)
    assertEquals(chain.focus().andThen(chainGetter).index(0).getAll, List(1))
    assertEquals(chain.focus().andThen(chainGetter).index(1).getAll, Nil)

    val nec       = NonEmptyChain.one(1)
    val necGetter = Iso.id[NonEmptyChain[Int]].asGetter
    assertEquals(necGetter.index(0).getAll(nec), List(1))
    assertEquals(necGetter.index(1).getAll(nec), Nil)
    assertEquals(nec.focus().andThen(necGetter).index(0).getAll, List(1))
    assertEquals(nec.focus().andThen(necGetter).index(1).getAll, Nil)

    val nev       = NonEmptyVector.one(1)
    val nevGetter = Iso.id[NonEmptyVector[Int]].asGetter
    assertEquals(nevGetter.index(0).getAll(nev), List(1))
    assertEquals(nevGetter.index(1).getAll(nev), Nil)
    assertEquals(nev.focus().andThen(nevGetter).index(0).getAll, List(1))
    assertEquals(nev.focus().andThen(nevGetter).index(1).getAll, Nil)

    val nel       = NonEmptyList.one(1)
    val nelGetter = Iso.id[NonEmptyList[Int]].asGetter
    assertEquals(nelGetter.index(0).getAll(nel), List(1))
    assertEquals(nelGetter.index(1).getAll(nel), Nil)
    assertEquals(nel.focus().andThen(nelGetter).index(0).getAll, List(1))
    assertEquals(nel.focus().andThen(nelGetter).index(1).getAll, Nil)
  }
}
