package monocle

import cats.arrow.{Category, Choice, Compose}
import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}
import monocle.macros.GenLens

import scala.collection.immutable

class SetterSpec extends MonocleSuite {
  def eachL[A]: Setter[List[A], A] = PSetter.fromFunctor[List, A, A]
  def even[A]: Setter[List[A], A] =
    filterIndex[List[A], Int, A](_ % 2 == 0).asSetter

  def eachLi: Setter[List[Int], Int]             = eachL[Int]
  def eachL2[A, B]: Setter[List[(A, B)], (A, B)] = eachL[(A, B)]

  // test implicit resolution of type classes

  test("Setter has a Compose instance") {
    assertEquals(
      Compose[Setter]
        .compose(eachL[Int], eachL[List[Int]])
        .replace(3)(List(List(1, 2, 3), List(4))),
      List(List(3, 3, 3), List(3))
    )
  }

  test("Setter has a Category instance") {
    assertEquals(Category[Setter].id[Int].modify(_ + 1)(3), 4)
  }

  test("Setter has a Choice instance") {
    assertEquals(
      Choice[Setter]
        .choice(eachL[Int], even[Int])
        .modify(_ + 1)(Right(List(1, 2, 3, 4))),
      Right(List(2, 2, 4, 4))
    )
  }

  test("set") {
    assertEquals(eachLi.replace(0)(List(1, 2, 3, 4)), List(0, 0, 0, 0))
  }

  test("modify") {
    assertEquals(eachLi.modify(_ + 1)(List(1, 2, 3, 4)), List(2, 3, 4, 5))
  }

  test("some") {
    case class SomeTest(x: Int, y: Option[Int])
    val obj = SomeTest(1, Some(2))

    val setter = GenLens[SomeTest](_.y).asSetter

    assertEquals(setter.some.replace(3)(obj), SomeTest(1, Some(3)))
    assertEquals(obj.applySetter(setter).some.replace(3), SomeTest(1, Some(3)))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val setter = GenLens[SomeTest](_.y).asSetter

    assertEquals(setter.withDefault(0).modify(_ + 1)(objSome), SomeTest(1, Some(3)))
    assertEquals(setter.withDefault(0).modify(_ + 1)(objNone), SomeTest(1, Some(1)))

    assertEquals(objNone.applySetter(setter).withDefault(0).modify(_ + 1), SomeTest(1, Some(1)))
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val setter = GenLens[SomeTest](_.y).asSetter

    assertEquals(setter.each.replace(3)(obj), SomeTest(1, List(3, 3, 3)))
    assertEquals(obj.applySetter(setter).each.replace(3), SomeTest(1, List(3, 3, 3)))
  }

  test("at") {
    val tuple2       = (1, 2)
    val tuple2Setter = Setter.id[(Int, Int)]
    assertEquals(tuple2Setter.at(1).replace(2)(tuple2), (2, 2))
    assertEquals(tuple2Setter.at(2).replace(3)(tuple2), (1, 3))
    assertEquals(tuple2.applySetter(tuple2Setter).at(1).replace(2), (2, 2))
    assertEquals(tuple2.applySetter(tuple2Setter).at(2).replace(3), (1, 3))

    val tuple3       = (1, 2, 3)
    val tuple3Setter = Setter.id[(Int, Int, Int)]
    assertEquals(tuple3Setter.at(1).replace(2)(tuple3), (2, 2, 3))
    assertEquals(tuple3Setter.at(2).replace(3)(tuple3), (1, 3, 3))
    assertEquals(tuple3Setter.at(3).replace(4)(tuple3), (1, 2, 4))
    assertEquals(tuple3.applySetter(tuple3Setter).at(1).replace(2), (2, 2, 3))
    assertEquals(tuple3.applySetter(tuple3Setter).at(2).replace(3), (1, 3, 3))
    assertEquals(tuple3.applySetter(tuple3Setter).at(3).replace(4), (1, 2, 4))

    val tuple4       = (1, 2, 3, 4)
    val tuple4Setter = Setter.id[(Int, Int, Int, Int)]
    assertEquals(tuple4Setter.at(1).replace(2)(tuple4), (2, 2, 3, 4))
    assertEquals(tuple4Setter.at(2).replace(3)(tuple4), (1, 3, 3, 4))
    assertEquals(tuple4Setter.at(3).replace(4)(tuple4), (1, 2, 4, 4))
    assertEquals(tuple4Setter.at(4).replace(1)(tuple4), (1, 2, 3, 1))
    assertEquals(tuple4.applySetter(tuple4Setter).at(1).replace(2), (2, 2, 3, 4))
    assertEquals(tuple4.applySetter(tuple4Setter).at(2).replace(3), (1, 3, 3, 4))
    assertEquals(tuple4.applySetter(tuple4Setter).at(3).replace(4), (1, 2, 4, 4))
    assertEquals(tuple4.applySetter(tuple4Setter).at(4).replace(1), (1, 2, 3, 1))

    val tuple5       = (1, 2, 3, 4, 5)
    val tuple5Setter = Setter.id[(Int, Int, Int, Int, Int)]
    assertEquals(tuple5Setter.at(1).replace(2)(tuple5), (2, 2, 3, 4, 5))
    assertEquals(tuple5Setter.at(2).replace(3)(tuple5), (1, 3, 3, 4, 5))
    assertEquals(tuple5Setter.at(3).replace(4)(tuple5), (1, 2, 4, 4, 5))
    assertEquals(tuple5Setter.at(4).replace(5)(tuple5), (1, 2, 3, 5, 5))
    assertEquals(tuple5Setter.at(5).replace(1)(tuple5), (1, 2, 3, 4, 1))
    assertEquals(tuple5.applySetter(tuple5Setter).at(1).replace(2), (2, 2, 3, 4, 5))
    assertEquals(tuple5.applySetter(tuple5Setter).at(2).replace(3), (1, 3, 3, 4, 5))
    assertEquals(tuple5.applySetter(tuple5Setter).at(3).replace(4), (1, 2, 4, 4, 5))
    assertEquals(tuple5.applySetter(tuple5Setter).at(4).replace(5), (1, 2, 3, 5, 5))
    assertEquals(tuple5.applySetter(tuple5Setter).at(5).replace(1), (1, 2, 3, 4, 1))

    val tuple6       = (1, 2, 3, 4, 5, 6)
    val tuple6Setter = Setter.id[(Int, Int, Int, Int, Int, Int)]
    assertEquals(tuple6Setter.at(1).replace(2)(tuple6), (2, 2, 3, 4, 5, 6))
    assertEquals(tuple6Setter.at(2).replace(3)(tuple6), (1, 3, 3, 4, 5, 6))
    assertEquals(tuple6Setter.at(3).replace(4)(tuple6), (1, 2, 4, 4, 5, 6))
    assertEquals(tuple6Setter.at(4).replace(5)(tuple6), (1, 2, 3, 5, 5, 6))
    assertEquals(tuple6Setter.at(5).replace(6)(tuple6), (1, 2, 3, 4, 6, 6))
    assertEquals(tuple6Setter.at(6).replace(1)(tuple6), (1, 2, 3, 4, 5, 1))
    assertEquals(tuple6.applySetter(tuple6Setter).at(1).replace(2), (2, 2, 3, 4, 5, 6))
    assertEquals(tuple6.applySetter(tuple6Setter).at(2).replace(3), (1, 3, 3, 4, 5, 6))
    assertEquals(tuple6.applySetter(tuple6Setter).at(3).replace(4), (1, 2, 4, 4, 5, 6))
    assertEquals(tuple6.applySetter(tuple6Setter).at(4).replace(5), (1, 2, 3, 5, 5, 6))
    assertEquals(tuple6.applySetter(tuple6Setter).at(5).replace(6), (1, 2, 3, 4, 6, 6))
    assertEquals(tuple6.applySetter(tuple6Setter).at(6).replace(1), (1, 2, 3, 4, 5, 1))

    val sortedMap       = immutable.SortedMap(1 -> "one")
    val sortedMapSetter = Setter.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapSetter.at(1).replace(Some("two"))(sortedMap), immutable.SortedMap(1 -> "two"))
    assertEquals(sortedMapSetter.at(0).replace(Some("two"))(sortedMap), immutable.SortedMap(0 -> "two", 1 -> "one"))
    assertEquals(sortedMap.applySetter(sortedMapSetter).at(1).replace(Some("two")), immutable.SortedMap(1 -> "two"))
    assertEquals(
      sortedMap.applySetter(sortedMapSetter).at(0).replace(Some("two")),
      immutable.SortedMap(0 -> "two", 1 -> "one")
    )

    val listMap       = immutable.ListMap(1 -> "one")
    val listMapSetter = Setter.id[immutable.ListMap[Int, String]]
    assertEquals(listMapSetter.at(1).replace(Some("two"))(listMap), immutable.ListMap(1 -> "two"))
    assertEquals(listMapSetter.at(0).replace(Some("two"))(listMap), immutable.ListMap(1 -> "one", 0 -> "two"))
    assertEquals(listMap.applySetter(listMapSetter).at(1).replace(Some("two")), immutable.ListMap(1 -> "two"))
    assertEquals(
      listMap.applySetter(listMapSetter).at(0).replace(Some("two")),
      immutable.ListMap(1 -> "one", 0 -> "two")
    )

    val map       = immutable.Map(1 -> "one")
    val mapSetter = Setter.id[Map[Int, String]]
    assertEquals(mapSetter.at(1).replace(Some("two"))(map), Map(1 -> "two"))
    assertEquals(mapSetter.at(0).replace(Some("two"))(map), Map(1 -> "one", 0 -> "two"))
    assertEquals(map.applySetter(mapSetter).at(1).replace(Some("two")), Map(1 -> "two"))
    assertEquals(map.applySetter(mapSetter).at(0).replace(Some("two")), Map(1 -> "one", 0 -> "two"))

    val set       = Set(1)
    val setSetter = Setter.id[Set[Int]]
    assertEquals(setSetter.at(1).replace(true)(set), Set(1))
    assertEquals(setSetter.at(2).replace(false)(set), Set(1))
    assertEquals(set.applySetter(setSetter).at(1).replace(true), Set(1))
    assertEquals(set.applySetter(setSetter).at(2).replace(false), Set(1))
  }

  test("index") {
    val list       = List(1)
    val listSetter = Setter.id[List[Int]]
    assertEquals(listSetter.index(0).replace(2)(list), List(2))
    assertEquals(listSetter.index(1).replace(2)(list), list)
    assertEquals(list.applySetter(listSetter).index(0).replace(2), List(2))
    assertEquals(list.applySetter(listSetter).index(1).replace(2), list)

    val lazyList       = LazyList(1)
    val lazyListSetter = Setter.id[LazyList[Int]]
    assertEquals(lazyListSetter.index(0).replace(2)(lazyList), LazyList(2))
    assertEquals(lazyListSetter.index(1).replace(2)(lazyList), lazyList)
    assertEquals(lazyList.applySetter(lazyListSetter).index(0).replace(2), LazyList(2))
    assertEquals(lazyList.applySetter(lazyListSetter).index(1).replace(2), lazyList)

    val listMap       = immutable.ListMap(1 -> "one")
    val listMapSetter = Setter.id[immutable.ListMap[Int, String]]
    assertEquals(listMapSetter.index(0).replace("two")(listMap), listMap)
    assertEquals(listMapSetter.index(1).replace("two")(listMap), immutable.ListMap(1 -> "two"))
    assertEquals(listMap.applySetter(listMapSetter).index(0).replace("two"), listMap)
    assertEquals(listMap.applySetter(listMapSetter).index(1).replace("two"), immutable.ListMap(1 -> "two"))

    val map       = Map(1 -> "one")
    val mapSetter = Setter.id[Map[Int, String]]
    assertEquals(mapSetter.index(0).replace("two")(map), map)
    assertEquals(mapSetter.index(1).replace("two")(map), Map(1 -> "two"))
    assertEquals(map.applySetter(mapSetter).index(0).replace("two"), map)
    assertEquals(map.applySetter(mapSetter).index(1).replace("two"), Map(1 -> "two"))

    val sortedMap       = immutable.SortedMap(1 -> "one")
    val sortedMapSetter = Setter.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapSetter.index(0).replace("two")(sortedMap), sortedMap)
    assertEquals(sortedMapSetter.index(1).replace("two")(sortedMap), immutable.SortedMap(1 -> "two"))
    assertEquals(sortedMap.applySetter(sortedMapSetter).index(0).replace("two"), sortedMap)
    assertEquals(sortedMap.applySetter(sortedMapSetter).index(1).replace("two"), immutable.SortedMap(1 -> "two"))

    val vector       = Vector(1)
    val vectorSetter = Setter.id[Vector[Int]]
    assertEquals(vectorSetter.index(0).replace(2)(vector), Vector(2))
    assertEquals(vectorSetter.index(1).replace(2)(vector), vector)
    assertEquals(vector.applySetter(vectorSetter).index(0).replace(2), Vector(2))
    assertEquals(vector.applySetter(vectorSetter).index(1).replace(2), vector)

    val chain       = Chain.one(1)
    val chainSetter = Setter.id[Chain[Int]]
    assertEquals(chainSetter.index(0).replace(2)(chain), Chain(2))
    assertEquals(chainSetter.index(1).replace(2)(chain), chain)
    assertEquals(chain.applySetter(chainSetter).index(0).replace(2), Chain(2))
    assertEquals(chain.applySetter(chainSetter).index(1).replace(2), chain)

    val nec       = NonEmptyChain.one(1)
    val necSetter = Setter.id[NonEmptyChain[Int]]
    assertEquals(necSetter.index(0).replace(2)(nec), NonEmptyChain(2))
    assertEquals(necSetter.index(1).replace(2)(nec), nec)
    assertEquals(nec.applySetter(necSetter).index(0).replace(2), NonEmptyChain(2))
    assertEquals(nec.applySetter(necSetter).index(1).replace(2), nec)

    val nev       = NonEmptyVector.one(1)
    val nevSetter = Setter.id[NonEmptyVector[Int]]
    assertEquals(nevSetter.index(0).replace(2)(nev), NonEmptyVector.one(2))
    assertEquals(nevSetter.index(1).replace(2)(nev), nev)
    assertEquals(nev.applySetter(nevSetter).index(0).replace(2), NonEmptyVector.one(2))
    assertEquals(nev.applySetter(nevSetter).index(1).replace(2), nev)

    val nel       = NonEmptyList.one(1)
    val nelSetter = Setter.id[NonEmptyList[Int]]
    assertEquals(nelSetter.index(0).replace(2)(nel), NonEmptyList.one(2))
    assertEquals(nelSetter.index(1).replace(2)(nel), nel)
    assertEquals(nel.applySetter(nelSetter).index(0).replace(2), NonEmptyList.one(2))
    assertEquals(nel.applySetter(nelSetter).index(1).replace(2), nel)
  }
}
