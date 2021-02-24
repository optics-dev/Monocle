package monocle

import cats.arrow.{Category, Choice, Compose}
import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}

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

    val setter = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue)).asSetter

    assertEquals(setter.some.replace(3)(obj), SomeTest(1, Some(3)))
    assertEquals(obj.focus().andThen(setter).some.replace(3), SomeTest(1, Some(3)))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val setter = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue)).asSetter

    assertEquals(setter.withDefault(0).modify(_ + 1)(objSome), SomeTest(1, Some(3)))
    assertEquals(setter.withDefault(0).modify(_ + 1)(objNone), SomeTest(1, Some(1)))

    assertEquals(objNone.focus().andThen(setter).withDefault(0).modify(_ + 1), SomeTest(1, Some(1)))
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val setter = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue)).asSetter

    assertEquals(setter.each.replace(3)(obj), SomeTest(1, List(3, 3, 3)))
    assertEquals(obj.focus().andThen(setter).each.replace(3), SomeTest(1, List(3, 3, 3)))
  }

  test("filter") {
    case class SomeTest(x: Int, y: Int)
    val obj = SomeTest(1, 2)

    val setter = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue)).asSetter

    assertEquals(setter.filter(_ > 0).replace(3)(obj), SomeTest(1, 3))
    assertEquals(obj.focus().andThen(setter).filter(_ > 0).replace(3), SomeTest(1, 3))
  }

  test("filterIndex") {
    case class SomeTest(x: Int, y: List[String])
    val obj = SomeTest(1, List("hello", "world"))

    val setter = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue)).asSetter

    assertEquals(setter.filterIndex((_: Int) > 0).replace("!")(obj), SomeTest(1, List("hello", "!")))
    assertEquals(obj.focus().andThen(setter).filterIndex((_: Int) > 0).replace("!"), SomeTest(1, List("hello", "!")))
  }

  test("at") {
    val sortedMap       = immutable.SortedMap(1 -> "one")
    val sortedMapSetter = Iso.id[immutable.SortedMap[Int, String]].asSetter
    assertEquals(sortedMapSetter.at(1).replace(Some("two"))(sortedMap), immutable.SortedMap(1 -> "two"))
    assertEquals(sortedMapSetter.at(0).replace(Some("two"))(sortedMap), immutable.SortedMap(0 -> "two", 1 -> "one"))
    assertEquals(sortedMap.focus().andThen(sortedMapSetter).at(1).replace(Some("two")), immutable.SortedMap(1 -> "two"))
    assertEquals(
      sortedMap.focus().andThen(sortedMapSetter).at(0).replace(Some("two")),
      immutable.SortedMap(0 -> "two", 1 -> "one")
    )

    val listMap       = immutable.ListMap(1 -> "one")
    val listMapSetter = Iso.id[immutable.ListMap[Int, String]].asSetter
    assertEquals(listMapSetter.at(1).replace(Some("two"))(listMap), immutable.ListMap(1 -> "two"))
    assertEquals(listMapSetter.at(0).replace(Some("two"))(listMap), immutable.ListMap(1 -> "one", 0 -> "two"))
    assertEquals(listMap.focus().andThen(listMapSetter).at(1).replace(Some("two")), immutable.ListMap(1 -> "two"))
    assertEquals(
      listMap.focus().andThen(listMapSetter).at(0).replace(Some("two")),
      immutable.ListMap(1 -> "one", 0 -> "two")
    )

    val map       = immutable.Map(1 -> "one")
    val mapSetter = Iso.id[Map[Int, String]].asSetter
    assertEquals(mapSetter.at(1).replace(Some("two"))(map), Map(1 -> "two"))
    assertEquals(mapSetter.at(0).replace(Some("two"))(map), Map(1 -> "one", 0 -> "two"))
    assertEquals(map.focus().andThen(mapSetter).at(1).replace(Some("two")), Map(1 -> "two"))
    assertEquals(map.focus().andThen(mapSetter).at(0).replace(Some("two")), Map(1 -> "one", 0 -> "two"))

    val set       = Set(1)
    val setSetter = Iso.id[Set[Int]].asSetter
    assertEquals(setSetter.at(1).replace(true)(set), Set(1))
    assertEquals(setSetter.at(2).replace(false)(set), Set(1))
    assertEquals(set.focus().andThen(setSetter).at(1).replace(true), Set(1))
    assertEquals(set.focus().andThen(setSetter).at(2).replace(false), Set(1))
  }

  test("index") {
    val list       = List(1)
    val listSetter = Iso.id[List[Int]].asSetter
    assertEquals(listSetter.index(0).replace(2)(list), List(2))
    assertEquals(listSetter.index(1).replace(2)(list), list)
    assertEquals(list.focus().andThen(listSetter).index(0).replace(2), List(2))
    assertEquals(list.focus().andThen(listSetter).index(1).replace(2), list)

    val lazyList       = LazyList(1)
    val lazyListSetter = Iso.id[LazyList[Int]].asSetter
    assertEquals(lazyListSetter.index(0).replace(2)(lazyList), LazyList(2))
    assertEquals(lazyListSetter.index(1).replace(2)(lazyList), lazyList)
    assertEquals(lazyList.focus().andThen(lazyListSetter).index(0).replace(2), LazyList(2))
    assertEquals(lazyList.focus().andThen(lazyListSetter).index(1).replace(2), lazyList)

    val listMap       = immutable.ListMap(1 -> "one")
    val listMapSetter = Iso.id[immutable.ListMap[Int, String]].asSetter
    assertEquals(listMapSetter.index(0).replace("two")(listMap), listMap)
    assertEquals(listMapSetter.index(1).replace("two")(listMap), immutable.ListMap(1 -> "two"))
    assertEquals(listMap.focus().andThen(listMapSetter).index(0).replace("two"), listMap)
    assertEquals(listMap.focus().andThen(listMapSetter).index(1).replace("two"), immutable.ListMap(1 -> "two"))

    val map       = Map(1 -> "one")
    val mapSetter = Iso.id[Map[Int, String]].asSetter
    assertEquals(mapSetter.index(0).replace("two")(map), map)
    assertEquals(mapSetter.index(1).replace("two")(map), Map(1 -> "two"))
    assertEquals(map.focus().andThen(mapSetter).index(0).replace("two"), map)
    assertEquals(map.focus().andThen(mapSetter).index(1).replace("two"), Map(1 -> "two"))

    val sortedMap       = immutable.SortedMap(1 -> "one")
    val sortedMapSetter = Iso.id[immutable.SortedMap[Int, String]].asSetter
    assertEquals(sortedMapSetter.index(0).replace("two")(sortedMap), sortedMap)
    assertEquals(sortedMapSetter.index(1).replace("two")(sortedMap), immutable.SortedMap(1 -> "two"))
    assertEquals(sortedMap.focus().andThen(sortedMapSetter).index(0).replace("two"), sortedMap)
    assertEquals(sortedMap.focus().andThen(sortedMapSetter).index(1).replace("two"), immutable.SortedMap(1 -> "two"))

    val vector       = Vector(1)
    val vectorSetter = Iso.id[Vector[Int]].asSetter
    assertEquals(vectorSetter.index(0).replace(2)(vector), Vector(2))
    assertEquals(vectorSetter.index(1).replace(2)(vector), vector)
    assertEquals(vector.focus().andThen(vectorSetter).index(0).replace(2), Vector(2))
    assertEquals(vector.focus().andThen(vectorSetter).index(1).replace(2), vector)

    val chain       = Chain.one(1)
    val chainSetter = Iso.id[Chain[Int]].asSetter
    assertEquals(chainSetter.index(0).replace(2)(chain), Chain(2))
    assertEquals(chainSetter.index(1).replace(2)(chain), chain)
    assertEquals(chain.focus().andThen(chainSetter).index(0).replace(2), Chain(2))
    assertEquals(chain.focus().andThen(chainSetter).index(1).replace(2), chain)

    val nec       = NonEmptyChain.one(1)
    val necSetter = Iso.id[NonEmptyChain[Int]].asSetter
    assertEquals(necSetter.index(0).replace(2)(nec), NonEmptyChain(2))
    assertEquals(necSetter.index(1).replace(2)(nec), nec)
    assertEquals(nec.focus().andThen(necSetter).index(0).replace(2), NonEmptyChain(2))
    assertEquals(nec.focus().andThen(necSetter).index(1).replace(2), nec)

    val nev       = NonEmptyVector.one(1)
    val nevSetter = Iso.id[NonEmptyVector[Int]].asSetter
    assertEquals(nevSetter.index(0).replace(2)(nev), NonEmptyVector.one(2))
    assertEquals(nevSetter.index(1).replace(2)(nev), nev)
    assertEquals(nev.focus().andThen(nevSetter).index(0).replace(2), NonEmptyVector.one(2))
    assertEquals(nev.focus().andThen(nevSetter).index(1).replace(2), nev)

    val nel       = NonEmptyList.one(1)
    val nelSetter = Iso.id[NonEmptyList[Int]].asSetter
    assertEquals(nelSetter.index(0).replace(2)(nel), NonEmptyList.one(2))
    assertEquals(nelSetter.index(1).replace(2)(nel), nel)
    assertEquals(nel.focus().andThen(nelSetter).index(0).replace(2), NonEmptyList.one(2))
    assertEquals(nel.focus().andThen(nelSetter).index(1).replace(2), nel)
  }
}
