package monocle

import monocle.law.discipline.{OptionalTests, SetterTests, TraversalTests}
import cats.arrow.{Category, Choice, Compose}
import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}

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

  test("replaceOption") {
    assertEquals(headOptionI.replaceOption(0)(List(1, 2, 3, 4)), Some(List(0, 2, 3, 4)))
    assertEquals(headOptionI.replaceOption(0)(Nil), None)
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

  test("orElse") {
    sealed trait User

    case class Editor(id: Int, favoriteFont: String) extends User
    case class Reader(id: Int, isPremium: Boolean)   extends User

    val idEditor = Optional[User, Int] {
      case x: Editor => Some(x.id)
      case _         => None
    }(newId => {
      case x: Editor => x.copy(id = newId)
      case other     => other
    })
    val idReader = Optional[User, Int] {
      case x: Reader => Some(x.id)
      case _         => None
    }(newId => {
      case x: Reader => x.copy(id = newId)
      case other     => other
    })

    val id = idEditor.orElse(idReader)

    assertEquals(id.getOption(Editor(1, "Comic Sans")), Some(1))
    assertEquals(id.getOption(Reader(1, false)), Some(1))

    assertEquals(id.replace(5)(Editor(1, "Comic Sans")), Editor(5, "Comic Sans"))
    assertEquals(id.replace(5)(Reader(1, false)), Reader(5, false))
  }

  test("some") {
    case class SomeTest(x: Int, y: Option[Int])
    val obj = SomeTest(1, Some(2))

    val optional = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue)).asOptional

    assertEquals(optional.some.getOption(obj), Some(2))
    assertEquals(obj.optics.andThen(optional).some.getOption, Some(2))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val optional = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue)).asOptional

    assertEquals(optional.withDefault(0).getOption(objSome), Some(2))
    assertEquals(optional.withDefault(0).getOption(objNone), Some(0))

    assertEquals(objNone.optics.andThen(optional).withDefault(0).getOption, Some(0))
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val optional = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue)).asOptional

    assertEquals(optional.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.optics.andThen(optional).each.getAll, List(1, 2, 3))
  }

  test("at") {
    val tuple2         = (1, 2)
    val tuple2Optional = Iso.id[(Int, Int)].asOptional
    assertEquals(tuple2Optional.at(1).getOption(tuple2), Some(1))
    assertEquals(tuple2Optional.at(2).getOption(tuple2), Some(2))
    assertEquals(tuple2.optics.andThen(tuple2Optional).at(1).getOption, Some(1))
    assertEquals(tuple2.optics.andThen(tuple2Optional).at(2).getOption, Some(2))

    val tuple3         = (1, 2, 3)
    val tuple3Optional = Iso.id[(Int, Int, Int)].asOptional
    assertEquals(tuple3Optional.at(1).getOption(tuple3), Some(1))
    assertEquals(tuple3Optional.at(2).getOption(tuple3), Some(2))
    assertEquals(tuple3Optional.at(3).getOption(tuple3), Some(3))
    assertEquals(tuple3.optics.andThen(tuple3Optional).at(1).getOption, Some(1))
    assertEquals(tuple3.optics.andThen(tuple3Optional).at(2).getOption, Some(2))
    assertEquals(tuple3.optics.andThen(tuple3Optional).at(3).getOption, Some(3))

    val tuple4         = (1, 2, 3, 4)
    val tuple4Optional = Iso.id[(Int, Int, Int, Int)].asOptional
    assertEquals(tuple4Optional.at(1).getOption(tuple4), Some(1))
    assertEquals(tuple4Optional.at(2).getOption(tuple4), Some(2))
    assertEquals(tuple4Optional.at(3).getOption(tuple4), Some(3))
    assertEquals(tuple4Optional.at(4).getOption(tuple4), Some(4))
    assertEquals(tuple4.optics.andThen(tuple4Optional).at(1).getOption, Some(1))
    assertEquals(tuple4.optics.andThen(tuple4Optional).at(2).getOption, Some(2))
    assertEquals(tuple4.optics.andThen(tuple4Optional).at(3).getOption, Some(3))
    assertEquals(tuple4.optics.andThen(tuple4Optional).at(4).getOption, Some(4))

    val tuple5         = (1, 2, 3, 4, 5)
    val tuple5Optional = Iso.id[(Int, Int, Int, Int, Int)].asOptional
    assertEquals(tuple5Optional.at(1).getOption(tuple5), Some(1))
    assertEquals(tuple5Optional.at(2).getOption(tuple5), Some(2))
    assertEquals(tuple5Optional.at(3).getOption(tuple5), Some(3))
    assertEquals(tuple5Optional.at(4).getOption(tuple5), Some(4))
    assertEquals(tuple5Optional.at(5).getOption(tuple5), Some(5))
    assertEquals(tuple5.optics.andThen(tuple5Optional).at(1).getOption, Some(1))
    assertEquals(tuple5.optics.andThen(tuple5Optional).at(2).getOption, Some(2))
    assertEquals(tuple5.optics.andThen(tuple5Optional).at(3).getOption, Some(3))
    assertEquals(tuple5.optics.andThen(tuple5Optional).at(4).getOption, Some(4))
    assertEquals(tuple5.optics.andThen(tuple5Optional).at(5).getOption, Some(5))

    val tuple6         = (1, 2, 3, 4, 5, 6)
    val tuple6Optional = Iso.id[(Int, Int, Int, Int, Int, Int)].asOptional
    assertEquals(tuple6Optional.at(1).getOption(tuple6), Some(1))
    assertEquals(tuple6Optional.at(2).getOption(tuple6), Some(2))
    assertEquals(tuple6Optional.at(3).getOption(tuple6), Some(3))
    assertEquals(tuple6Optional.at(4).getOption(tuple6), Some(4))
    assertEquals(tuple6Optional.at(5).getOption(tuple6), Some(5))
    assertEquals(tuple6Optional.at(6).getOption(tuple6), Some(6))
    assertEquals(tuple6.optics.andThen(tuple6Optional).at(1).getOption, Some(1))
    assertEquals(tuple6.optics.andThen(tuple6Optional).at(2).getOption, Some(2))
    assertEquals(tuple6.optics.andThen(tuple6Optional).at(3).getOption, Some(3))
    assertEquals(tuple6.optics.andThen(tuple6Optional).at(4).getOption, Some(4))
    assertEquals(tuple6.optics.andThen(tuple6Optional).at(5).getOption, Some(5))
    assertEquals(tuple6.optics.andThen(tuple6Optional).at(6).getOption, Some(6))

    val sortedMap         = immutable.SortedMap(1 -> "one")
    val sortedMapOptional = Iso.id[immutable.SortedMap[Int, String]].asOptional
    assertEquals(sortedMapOptional.at(1).getOption(sortedMap), Some(Some("one")))
    assertEquals(sortedMapOptional.at(0).getOption(sortedMap), Some(None))
    assertEquals(sortedMap.optics.andThen(sortedMapOptional).at(1).getOption, Some(Some("one")))
    assertEquals(sortedMap.optics.andThen(sortedMapOptional).at(0).getOption, Some(None))

    val listMap         = immutable.ListMap(1 -> "one")
    val listMapOptional = Iso.id[immutable.ListMap[Int, String]].asOptional
    assertEquals(listMapOptional.at(1).getOption(listMap), Some(Some("one")))
    assertEquals(listMapOptional.at(0).getOption(listMap), Some(None))
    assertEquals(listMap.optics.andThen(listMapOptional).at(1).getOption, Some(Some("one")))
    assertEquals(listMap.optics.andThen(listMapOptional).at(0).getOption, Some(None))

    val map         = immutable.Map(1 -> "one")
    val mapOptional = Iso.id[Map[Int, String]].asOptional
    assertEquals(mapOptional.at(1).getOption(map), Some(Some("one")))
    assertEquals(mapOptional.at(0).getOption(map), Some(None))
    assertEquals(map.optics.andThen(mapOptional).at(1).getOption, Some(Some("one")))
    assertEquals(map.optics.andThen(mapOptional).at(0).getOption, Some(None))

    val set         = Set(1)
    val setOptional = Iso.id[Set[Int]].asOptional
    assertEquals(setOptional.at(1).getOption(set), Some(true))
    assertEquals(setOptional.at(0).getOption(set), Some(false))
    assertEquals(set.optics.andThen(setOptional).at(1).getOption, Some(true))
    assertEquals(set.optics.andThen(setOptional).at(0).getOption, Some(false))
  }

  test("index") {
    val list         = List(1)
    val listOptional = Iso.id[List[Int]].asOptional
    assertEquals(listOptional.index(0).getOption(list), Some(1))
    assertEquals(listOptional.index(1).getOption(list), None)
    assertEquals(list.optics.andThen(listOptional).index(0).getOption, Some(1))
    assertEquals(list.optics.andThen(listOptional).index(1).getOption, None)

    val lazyList         = LazyList(1)
    val lazyListOptional = Iso.id[LazyList[Int]].asOptional
    assertEquals(lazyListOptional.index(0).getOption(lazyList), Some(1))
    assertEquals(lazyListOptional.index(1).getOption(lazyList), None)
    assertEquals(lazyList.optics.andThen(lazyListOptional).index(0).getOption, Some(1))
    assertEquals(lazyList.optics.andThen(lazyListOptional).index(1).getOption, None)

    val listMap         = immutable.ListMap(1 -> "one")
    val listMapOptional = Iso.id[immutable.ListMap[Int, String]].asOptional
    assertEquals(listMapOptional.index(0).getOption(listMap), None)
    assertEquals(listMapOptional.index(1).getOption(listMap), Some("one"))
    assertEquals(listMap.optics.andThen(listMapOptional).index(0).getOption, None)
    assertEquals(listMap.optics.andThen(listMapOptional).index(1).getOption, Some("one"))

    val map         = Map(1 -> "one")
    val mapOptional = Iso.id[Map[Int, String]].asOptional
    assertEquals(mapOptional.index(0).getOption(map), None)
    assertEquals(mapOptional.index(1).getOption(map), Some("one"))
    assertEquals(map.optics.andThen(mapOptional).index(0).getOption, None)
    assertEquals(map.optics.andThen(mapOptional).index(1).getOption, Some("one"))

    val sortedMap         = immutable.SortedMap(1 -> "one")
    val sortedMapOptional = Iso.id[immutable.SortedMap[Int, String]].asOptional
    assertEquals(sortedMapOptional.index(0).getOption(sortedMap), None)
    assertEquals(sortedMapOptional.index(1).getOption(sortedMap), Some("one"))
    assertEquals(sortedMap.optics.andThen(sortedMapOptional).index(0).getOption, None)
    assertEquals(sortedMap.optics.andThen(sortedMapOptional).index(1).getOption, Some("one"))

    val vector         = Vector(1)
    val vectorOptional = Iso.id[Vector[Int]].asOptional
    assertEquals(vectorOptional.index(0).getOption(vector), Some(1))
    assertEquals(vectorOptional.index(1).getOption(vector), None)
    assertEquals(vector.optics.andThen(vectorOptional).index(0).getOption, Some(1))
    assertEquals(vector.optics.andThen(vectorOptional).index(1).getOption, None)

    val chain         = Chain.one(1)
    val chainOptional = Iso.id[Chain[Int]].asOptional
    assertEquals(chainOptional.index(0).getOption(chain), Some(1))
    assertEquals(chainOptional.index(1).getOption(chain), None)
    assertEquals(chain.optics.andThen(chainOptional).index(0).getOption, Some(1))
    assertEquals(chain.optics.andThen(chainOptional).index(1).getOption, None)

    val nec         = NonEmptyChain.one(1)
    val necOptional = Iso.id[NonEmptyChain[Int]].asOptional
    assertEquals(necOptional.index(0).getOption(nec), Some(1))
    assertEquals(necOptional.index(1).getOption(nec), None)
    assertEquals(nec.optics.andThen(necOptional).index(0).getOption, Some(1))
    assertEquals(nec.optics.andThen(necOptional).index(1).getOption, None)

    val nev         = NonEmptyVector.one(1)
    val nevOptional = Iso.id[NonEmptyVector[Int]].asOptional
    assertEquals(nevOptional.index(0).getOption(nev), Some(1))
    assertEquals(nevOptional.index(1).getOption(nev), None)
    assertEquals(nev.optics.andThen(nevOptional).index(0).getOption, Some(1))
    assertEquals(nev.optics.andThen(nevOptional).index(1).getOption, None)

    val nel         = NonEmptyList.one(1)
    val nelOptional = Iso.id[NonEmptyList[Int]].asOptional
    assertEquals(nelOptional.index(0).getOption(nel), Some(1))
    assertEquals(nelOptional.index(1).getOption(nel), None)
    assertEquals(nel.optics.andThen(nelOptional).index(0).getOption, Some(1))
    assertEquals(nel.optics.andThen(nelOptional).index(1).getOption, None)
  }

  test("filter") {
    val positiveNumbers = Traversal.fromTraverse[List, Int].andThen(Optional.filter[Int](_ >= 0))

    assertEquals(positiveNumbers.getAll(List(1, 2, -3, 4, -5)), List(1, 2, 4))
    assertEquals(positiveNumbers.modify(_ * 10)(List(1, 2, -3, 4, -5)), List(10, 20, -3, 40, -5))
  }

  test("filterIndex") {
    case class SomeTest(x: Int, y: List[String])
    val obj = SomeTest(1, List("hello", "world"))

    val optional = Lens((_: SomeTest).y)(newValue => _.copy(y = newValue)).asOptional

    assertEquals(optional.filterIndex((_: Int) > 0).getAll(obj), List("world"))
    assertEquals(obj.optics.andThen(optional).filterIndex((_: Int) > 0).getAll, List("world"))
  }

  test("filter can break the fusion property") {
    val positiveNumbers = Traversal.fromTraverse[List, Int].andThen(Optional.filter[Int](_ >= 0))
    val list            = List(1, 5, -3)
    val firstStep       = positiveNumbers.modify(_ - 3)(list)
    val secondStep      = positiveNumbers.modify(_ * 2)(firstStep)
    val bothSteps       = positiveNumbers.modify(x => (x - 3) * 2)(list)
    assertEquals(firstStep, List(-2, 2, -3))
    assertEquals(secondStep, List(-2, 4, -3))
    assertEquals(bothSteps, List(-4, 4, -3))
    assertNotEquals(secondStep, bothSteps)
  }
}
