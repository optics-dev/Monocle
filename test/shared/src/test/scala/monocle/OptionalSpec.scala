package monocle

import monocle.law.discipline.{OptionalTests, SetterTests, TraversalTests}
import cats.arrow.{Category, Choice, Compose}
import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}
import monocle.macros.GenLens

import scala.collection.immutable

class OptionalSpec extends MonocleSuite {
  def headOption[A]: Optional[List[A], A] =
    Optional[List[A], A](_.headOption) { a =>
      {
        case x :: xs => a :: xs
        case Nil     => Nil
      }
    }

  def headOptionI: Optional[List[Int], Int]             = headOption[Int]
  def headOption2[A, B]: Optional[List[(A, B)], (A, B)] = headOption[(A, B)]

  checkAll("apply Optional", OptionalTests(headOptionI))

  checkAll("optional.asTraversal", TraversalTests(headOptionI.asTraversal))
  checkAll("optional.asSetter", SetterTests(headOptionI.asSetter))

  checkAll("first", OptionalTests(headOptionI.first[Boolean]))
  checkAll("second", OptionalTests(headOptionI.second[Boolean]))

  test("void") {
    assertEquals(Optional.void.getOption("hello"), None)
    assertEquals(Optional.void.replace(5)("hello"), "hello")
  }

  // test implicit resolution of type classes

  test("Optional has a Compose instance") {
    assertEquals(
      Compose[Optional]
        .compose(headOptionI, headOption[List[Int]])
        .getOption(List(List(1, 2, 3), List(4))),
      Some(1)
    )
  }

  test("Optional has a Category instance") {
    assertEquals(Category[Optional].id[Int].getOption(3), Some(3))
  }

  test("Optional has a Choice instance") {
    assertEquals(
      Choice[Optional]
        .choice(headOptionI, Category[Optional].id[Int])
        .getOption(Left(List(1, 2, 3))),
      Some(1)
    )
  }

  test("getOption") {
    assertEquals(headOptionI.getOption(List(1, 2, 3, 4)), Some(1))
    assertEquals(headOptionI.getOption(Nil), None)
  }

  test("isEmpty") {
    assertEquals(headOptionI.isEmpty(List(1, 2, 3, 4)), false)
    assertEquals(headOptionI.isEmpty(Nil), true)
  }

  test("nonEmpty") {
    assertEquals(headOptionI.nonEmpty(List(1, 2, 3, 4)), true)
    assertEquals(headOptionI.nonEmpty(Nil), false)
  }

  test("find") {
    assertEquals(headOptionI.find(_ > 0)(List(1, 2, 3, 4)), Some(1))
    assertEquals(headOptionI.find(_ > 9)(List(1, 2, 3, 4)), None)
  }

  test("exist") {
    assertEquals(headOptionI.exist(_ > 0)(List(1, 2, 3, 4)), true)
    assertEquals(headOptionI.exist(_ > 9)(List(1, 2, 3, 4)), false)
    assertEquals(headOptionI.exist(_ > 9)(Nil), false)
  }

  test("all") {
    assertEquals(headOptionI.all(_ > 2)(List(1, 2, 3, 4)), false)
    assertEquals(headOptionI.all(_ > 0)(List(1, 2, 3, 4)), true)
    assertEquals(headOptionI.all(_ > 0)(Nil), true)
  }

  test("set") {
    assertEquals(headOptionI.replace(0)(List(1, 2, 3, 4)), List(0, 2, 3, 4))
    assertEquals(headOptionI.replace(0)(Nil), Nil)
  }

  test("setOption") {
    assertEquals(headOptionI.setOption(0)(List(1, 2, 3, 4)), Some(List(0, 2, 3, 4)))
    assertEquals(headOptionI.setOption(0)(Nil), None)
  }

  test("modify") {
    assertEquals(headOptionI.modify(_ + 1)(List(1, 2, 3, 4)), List(2, 2, 3, 4))
    assertEquals(headOptionI.modify(_ + 1)(Nil), Nil)
  }

  test("modifyOption") {
    assertEquals(headOptionI.modifyOption(_ + 1)(List(1, 2, 3, 4)), Some(List(2, 2, 3, 4)))
    assertEquals(headOptionI.modifyOption(_ + 1)(Nil), None)
  }

  test("to") {
    assertEquals(headOptionI.to(_.toString()).getAll(List(1, 2, 3)), List("1"))
  }

  test("some") {
    case class SomeTest(x: Int, y: Option[Int])
    val obj = SomeTest(1, Some(2))

    val optional = GenLens[SomeTest](_.y).asOptional

    assertEquals(optional.some.getOption(obj), Some(2))
    assertEquals(obj.applyOptional(optional).some.getOption, Some(2))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val optional = GenLens[SomeTest](_.y).asOptional

    assertEquals(optional.withDefault(0).getOption(objSome), Some(2))
    assertEquals(optional.withDefault(0).getOption(objNone), Some(0))

    assertEquals(objNone.applyOptional(optional).withDefault(0).getOption, Some(0))
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val optional = GenLens[SomeTest](_.y).asOptional

    assertEquals(optional.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.applyOptional(optional).each.getAll, List(1, 2, 3))
  }

  test("at") {
    val tuple2         = (1, 2)
    val tuple2Optional = Optional.id[(Int, Int)]
    assertEquals(tuple2Optional.at(1).getOption(tuple2), Some(1))
    assertEquals(tuple2Optional.at(2).getOption(tuple2), Some(2))
    assertEquals(tuple2.applyOptional(tuple2Optional).at(1).getOption, Some(1))
    assertEquals(tuple2.applyOptional(tuple2Optional).at(2).getOption, Some(2))

    val tuple3         = (1, 2, 3)
    val tuple3Optional = Optional.id[(Int, Int, Int)]
    assertEquals(tuple3Optional.at(1).getOption(tuple3), Some(1))
    assertEquals(tuple3Optional.at(2).getOption(tuple3), Some(2))
    assertEquals(tuple3Optional.at(3).getOption(tuple3), Some(3))
    assertEquals(tuple3.applyOptional(tuple3Optional).at(1).getOption, Some(1))
    assertEquals(tuple3.applyOptional(tuple3Optional).at(2).getOption, Some(2))
    assertEquals(tuple3.applyOptional(tuple3Optional).at(3).getOption, Some(3))

    val tuple4         = (1, 2, 3, 4)
    val tuple4Optional = Optional.id[(Int, Int, Int, Int)]
    assertEquals(tuple4Optional.at(1).getOption(tuple4), Some(1))
    assertEquals(tuple4Optional.at(2).getOption(tuple4), Some(2))
    assertEquals(tuple4Optional.at(3).getOption(tuple4), Some(3))
    assertEquals(tuple4Optional.at(4).getOption(tuple4), Some(4))
    assertEquals(tuple4.applyOptional(tuple4Optional).at(1).getOption, Some(1))
    assertEquals(tuple4.applyOptional(tuple4Optional).at(2).getOption, Some(2))
    assertEquals(tuple4.applyOptional(tuple4Optional).at(3).getOption, Some(3))
    assertEquals(tuple4.applyOptional(tuple4Optional).at(4).getOption, Some(4))

    val tuple5         = (1, 2, 3, 4, 5)
    val tuple5Optional = Optional.id[(Int, Int, Int, Int, Int)]
    assertEquals(tuple5Optional.at(1).getOption(tuple5), Some(1))
    assertEquals(tuple5Optional.at(2).getOption(tuple5), Some(2))
    assertEquals(tuple5Optional.at(3).getOption(tuple5), Some(3))
    assertEquals(tuple5Optional.at(4).getOption(tuple5), Some(4))
    assertEquals(tuple5Optional.at(5).getOption(tuple5), Some(5))
    assertEquals(tuple5.applyOptional(tuple5Optional).at(1).getOption, Some(1))
    assertEquals(tuple5.applyOptional(tuple5Optional).at(2).getOption, Some(2))
    assertEquals(tuple5.applyOptional(tuple5Optional).at(3).getOption, Some(3))
    assertEquals(tuple5.applyOptional(tuple5Optional).at(4).getOption, Some(4))
    assertEquals(tuple5.applyOptional(tuple5Optional).at(5).getOption, Some(5))

    val tuple6         = (1, 2, 3, 4, 5, 6)
    val tuple6Optional = Optional.id[(Int, Int, Int, Int, Int, Int)]
    assertEquals(tuple6Optional.at(1).getOption(tuple6), Some(1))
    assertEquals(tuple6Optional.at(2).getOption(tuple6), Some(2))
    assertEquals(tuple6Optional.at(3).getOption(tuple6), Some(3))
    assertEquals(tuple6Optional.at(4).getOption(tuple6), Some(4))
    assertEquals(tuple6Optional.at(5).getOption(tuple6), Some(5))
    assertEquals(tuple6Optional.at(6).getOption(tuple6), Some(6))
    assertEquals(tuple6.applyOptional(tuple6Optional).at(1).getOption, Some(1))
    assertEquals(tuple6.applyOptional(tuple6Optional).at(2).getOption, Some(2))
    assertEquals(tuple6.applyOptional(tuple6Optional).at(3).getOption, Some(3))
    assertEquals(tuple6.applyOptional(tuple6Optional).at(4).getOption, Some(4))
    assertEquals(tuple6.applyOptional(tuple6Optional).at(5).getOption, Some(5))
    assertEquals(tuple6.applyOptional(tuple6Optional).at(6).getOption, Some(6))

    val sortedMap         = immutable.SortedMap(1 -> "one")
    val sortedMapOptional = Optional.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapOptional.at(1).getOption(sortedMap), Some(Some("one")))
    assertEquals(sortedMapOptional.at(0).getOption(sortedMap), Some(None))
    assertEquals(sortedMap.applyOptional(sortedMapOptional).at(1).getOption, Some(Some("one")))
    assertEquals(sortedMap.applyOptional(sortedMapOptional).at(0).getOption, Some(None))

    val listMap         = immutable.ListMap(1 -> "one")
    val listMapOptional = Optional.id[immutable.ListMap[Int, String]]
    assertEquals(listMapOptional.at(1).getOption(listMap), Some(Some("one")))
    assertEquals(listMapOptional.at(0).getOption(listMap), Some(None))
    assertEquals(listMap.applyOptional(listMapOptional).at(1).getOption, Some(Some("one")))
    assertEquals(listMap.applyOptional(listMapOptional).at(0).getOption, Some(None))

    val map         = immutable.Map(1 -> "one")
    val mapOptional = Optional.id[Map[Int, String]]
    assertEquals(mapOptional.at(1).getOption(map), Some(Some("one")))
    assertEquals(mapOptional.at(0).getOption(map), Some(None))
    assertEquals(map.applyOptional(mapOptional).at(1).getOption, Some(Some("one")))
    assertEquals(map.applyOptional(mapOptional).at(0).getOption, Some(None))

    val set         = Set(1)
    val setOptional = Optional.id[Set[Int]]
    assertEquals(setOptional.at(1).getOption(set), Some(true))
    assertEquals(setOptional.at(0).getOption(set), Some(false))
    assertEquals(set.applyOptional(setOptional).at(1).getOption, Some(true))
    assertEquals(set.applyOptional(setOptional).at(0).getOption, Some(false))
  }

  test("index") {
    val list         = List(1)
    val listOptional = Optional.id[List[Int]]
    assertEquals(listOptional.index(0).getOption(list), Some(1))
    assertEquals(listOptional.index(1).getOption(list), None)
    assertEquals(list.applyOptional(listOptional).index(0).getOption, Some(1))
    assertEquals(list.applyOptional(listOptional).index(1).getOption, None)

    val lazyList         = LazyList(1)
    val lazyListOptional = Optional.id[LazyList[Int]]
    assertEquals(lazyListOptional.index(0).getOption(lazyList), Some(1))
    assertEquals(lazyListOptional.index(1).getOption(lazyList), None)
    assertEquals(lazyList.applyOptional(lazyListOptional).index(0).getOption, Some(1))
    assertEquals(lazyList.applyOptional(lazyListOptional).index(1).getOption, None)

    val listMap         = immutable.ListMap(1 -> "one")
    val listMapOptional = Optional.id[immutable.ListMap[Int, String]]
    assertEquals(listMapOptional.index(0).getOption(listMap), None)
    assertEquals(listMapOptional.index(1).getOption(listMap), Some("one"))
    assertEquals(listMap.applyOptional(listMapOptional).index(0).getOption, None)
    assertEquals(listMap.applyOptional(listMapOptional).index(1).getOption, Some("one"))

    val map         = Map(1 -> "one")
    val mapOptional = Optional.id[Map[Int, String]]
    assertEquals(mapOptional.index(0).getOption(map), None)
    assertEquals(mapOptional.index(1).getOption(map), Some("one"))
    assertEquals(map.applyOptional(mapOptional).index(0).getOption, None)
    assertEquals(map.applyOptional(mapOptional).index(1).getOption, Some("one"))

    val sortedMap         = immutable.SortedMap(1 -> "one")
    val sortedMapOptional = Optional.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapOptional.index(0).getOption(sortedMap), None)
    assertEquals(sortedMapOptional.index(1).getOption(sortedMap), Some("one"))
    assertEquals(sortedMap.applyOptional(sortedMapOptional).index(0).getOption, None)
    assertEquals(sortedMap.applyOptional(sortedMapOptional).index(1).getOption, Some("one"))

    val vector         = Vector(1)
    val vectorOptional = Optional.id[Vector[Int]]
    assertEquals(vectorOptional.index(0).getOption(vector), Some(1))
    assertEquals(vectorOptional.index(1).getOption(vector), None)
    assertEquals(vector.applyOptional(vectorOptional).index(0).getOption, Some(1))
    assertEquals(vector.applyOptional(vectorOptional).index(1).getOption, None)

    val chain         = Chain.one(1)
    val chainOptional = Optional.id[Chain[Int]]
    assertEquals(chainOptional.index(0).getOption(chain), Some(1))
    assertEquals(chainOptional.index(1).getOption(chain), None)
    assertEquals(chain.applyOptional(chainOptional).index(0).getOption, Some(1))
    assertEquals(chain.applyOptional(chainOptional).index(1).getOption, None)

    val nec         = NonEmptyChain.one(1)
    val necOptional = Optional.id[NonEmptyChain[Int]]
    assertEquals(necOptional.index(0).getOption(nec), Some(1))
    assertEquals(necOptional.index(1).getOption(nec), None)
    assertEquals(nec.applyOptional(necOptional).index(0).getOption, Some(1))
    assertEquals(nec.applyOptional(necOptional).index(1).getOption, None)

    val nev         = NonEmptyVector.one(1)
    val nevOptional = Optional.id[NonEmptyVector[Int]]
    assertEquals(nevOptional.index(0).getOption(nev), Some(1))
    assertEquals(nevOptional.index(1).getOption(nev), None)
    assertEquals(nev.applyOptional(nevOptional).index(0).getOption, Some(1))
    assertEquals(nev.applyOptional(nevOptional).index(1).getOption, None)

    val nel         = NonEmptyList.one(1)
    val nelOptional = Optional.id[NonEmptyList[Int]]
    assertEquals(nelOptional.index(0).getOption(nel), Some(1))
    assertEquals(nelOptional.index(1).getOption(nel), None)
    assertEquals(nel.applyOptional(nelOptional).index(0).getOption, Some(1))
    assertEquals(nel.applyOptional(nelOptional).index(1).getOption, None)
  }
}
