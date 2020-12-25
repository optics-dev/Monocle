package monocle

import monocle.law.discipline._
import monocle.macros.GenIso
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import cats.Eq
import cats.arrow.{Category, Compose}
import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}

import scala.collection.immutable

class IsoSpec extends MonocleSuite {
  val _nullary: Iso[Nullary, Unit] = Iso[Nullary, Unit](n => ()) { case () =>
    Nullary()
  }
  val _unary: Iso[Unary, Int] = Iso[Unary, Int](_.i)(Unary)
  val _binary: Iso[Binary, (String, Int)] =
    Iso[Binary, (String, Int)](b => (b.s, b.i))(Binary.tupled)
  val _quintary: Iso[Quintary, (Char, Boolean, String, Int, Double)] =
    Iso[Quintary, (Char, Boolean, String, Int, Double)](b => (b.c, b.b, b.s, b.i, b.f))(Quintary.tupled)

  case class IntWrapper(i: Int)
  implicit val intWrapperGen: Arbitrary[IntWrapper] = Arbitrary(arbitrary[Int].map(IntWrapper.apply))
  implicit val intWrapperEq                         = Eq.fromUniversalEquals[IntWrapper]

  case class IdWrapper[A](value: A)
  implicit def idWrapperGen[A: Arbitrary]: Arbitrary[IdWrapper[A]] =
    Arbitrary(arbitrary[A].map(IdWrapper.apply))
  implicit def idWrapperEq[A: Eq]: Eq[IdWrapper[A]] = Eq.fromUniversalEquals

  case object AnObject
  implicit val anObjectGen: Arbitrary[AnObject.type] = Arbitrary(Gen.const(AnObject))
  implicit val anObjectEq                            = Eq.fromUniversalEquals[AnObject.type]

  case class EmptyCase()
  implicit val emptyCaseGen: Arbitrary[EmptyCase] = Arbitrary(Gen.const(EmptyCase()))
  implicit val emptyCaseEq                        = Eq.fromUniversalEquals[EmptyCase]

  case class EmptyCaseType[A]()
  implicit def emptyCaseTypeGen[A]: Arbitrary[EmptyCaseType[A]] =
    Arbitrary(Gen.const(EmptyCaseType()))
  implicit def emptyCaseTypeEq[A] = Eq.fromUniversalEquals[EmptyCaseType[A]]

  val iso = Iso[IntWrapper, Int](_.i)(IntWrapper.apply)
  val involutedListReverse =
    Iso.involuted[List[Int]](_.reverse) // ∀ {T} -> List(ts: T*).reverse.reverse == List(ts: T*)
  val involutedTwoMinusN = Iso.involuted[Int](2 - _) //  ∀ {n : Int} -> n == 2 - (2 - n)

  checkAll("apply Iso", IsoTests(iso))
  checkAll("GenIso", IsoTests(GenIso[IntWrapper, Int]))
  checkAll("GenIso with type param", IsoTests(GenIso[IdWrapper[Int], Int]))
  checkAll("GenIso.unit object", IsoTests(GenIso.unit[AnObject.type]))
  checkAll("GenIso.unit empty case class", IsoTests(GenIso.unit[EmptyCase]))
  checkAll("GenIso.unit empty case class with type param", IsoTests(GenIso.unit[EmptyCaseType[Int]]))

  checkAll("Iso id", IsoTests(Iso.id[Int]))

  checkAll("Iso involutedListReverse", IsoTests(involutedListReverse))
  checkAll("Iso involutedTwoMinusN", IsoTests(involutedTwoMinusN))

  checkAll("Iso.asLens", LensTests(iso.asLens))
  checkAll("Iso.asPrism", PrismTests(iso.asPrism))
  checkAll("Iso.asOptional", OptionalTests(iso.asOptional))
  checkAll("Iso.asTraversal", TraversalTests(iso.asTraversal))
  checkAll("Iso.asSetter", SetterTests(iso.asSetter))

  checkAll("first", IsoTests(iso.first[Boolean]))
  checkAll("second", IsoTests(iso.second[Boolean]))
  checkAll("left", IsoTests(iso.left[Boolean]))
  checkAll("right", IsoTests(iso.right[Boolean]))

  // test implicit resolution of type classes

  test("Iso has a Compose instance") {
    assertEquals(Compose[Iso].compose(iso, iso.reverse).get(3), 3)
  }

  test("Iso has a Category instance") {
    assertEquals(Category[Iso].id[Int].get(3), 3)
  }

  test("mapping") {
    import cats.Id

    assertEquals(iso.mapping[Id].get(IntWrapper(3)), 3)
    assertEquals(iso.mapping[Id].reverseGet(3), IntWrapper(3))
  }

  test("apply") {
    assertEquals(_nullary(), Nullary())
    assertEquals(_unary(3), Unary(3))
    assertEquals(_binary("foo", 7), Binary("foo", 7))
    assertEquals(_quintary('x', true, "bar", 13, 0.4), Quintary('x', true, "bar", 13, 0.4))
  }

  test("unapply") {
    // format: off
assertEquals(    (Nullary() match { case _nullary(unit) => unit }) ,  (()))
    // format: on
    assertEquals((Unary(3) match { case _unary(value) => value * 2 }), 6)
    assertEquals((Binary("foo", 7) match { case _binary(s, i) => s + i }), "foo7")
    assertEquals(
      (Quintary('x', true, "bar", 13, 0.4) match {
        case _quintary(c, b, s, i, f) => "" + c + b + s + i + f
      }),
      "xtruebar130.4"
    )
  }

  test("get") {
    assertEquals(iso.get(IntWrapper(5)), 5)
  }

  test("reverseGet") {
    assertEquals(iso.reverseGet(5), IntWrapper(5))
  }

  test("find") {
    assertEquals(iso.find(_ > 5)(IntWrapper(9)), Some(9))
    assertEquals(iso.find(_ > 5)(IntWrapper(3)), None)
  }

  test("exist") {
    assertEquals(iso.exist(_ > 5)(IntWrapper(9)), true)
    assertEquals(iso.exist(_ > 5)(IntWrapper(3)), false)
  }

  test("set") {
    assertEquals(iso.replace(5)(IntWrapper(0)), IntWrapper(5))
  }

  test("modify") {
    assertEquals(iso.modify(_ + 1)(IntWrapper(0)), IntWrapper(1))
  }

  test("involuted") {
    assertEquals(involutedListReverse.get(List(1, 2, 3)), List(3, 2, 1))
    assertEquals(involutedListReverse.reverseGet(List(1, 2, 3)), List(3, 2, 1))

    assertEquals(involutedListReverse.reverse.get(List(1, 2, 3)), involutedListReverse.get(List(1, 2, 3)))
    assertEquals(involutedListReverse.reverse.reverseGet(List(1, 2, 3)), involutedListReverse.reverseGet(List(1, 2, 3)))

    assertEquals(involutedTwoMinusN.get(5), -3)
    assertEquals(involutedTwoMinusN.reverseGet(5), -3)

    assertEquals(involutedTwoMinusN.reverse.get(5), involutedTwoMinusN.get(5))
    assertEquals(involutedTwoMinusN.reverse.reverseGet(5), involutedTwoMinusN.reverseGet(5))
  }

  test("GenIso nullary equality".ignore) {
    assertEquals(GenIso.unit[Nullary], _nullary)
  }

  test("GenIso unary equality".ignore) {
    assertEquals(GenIso[Unary, Int], _unary)
  }

  test("GenIso binary equality".ignore) {
    assertEquals(GenIso.fields[Binary], _binary)
  }

  test("GenIso quintary equality".ignore) {
    assertEquals(GenIso.fields[Quintary], _quintary)
  }

  test("to") {
    assertEquals(iso.to(_.toString()).get(IntWrapper(5)), "5")
  }

  test("some") {
    case class SomeTest(y: Option[Int])
    val obj = SomeTest(Some(2))

    val iso = Iso[SomeTest, Option[Int]](_.y)(SomeTest)

    assertEquals(iso.some.getOption(obj), Some(2))
    assertEquals(obj.optics.andThen(iso).some.getOption, Some(2))
  }

  test("withDefault") {
    case class SomeTest(y: Option[Int])
    val objSome = SomeTest(Some(2))
    val objNone = SomeTest(None)

    val iso = Iso[SomeTest, Option[Int]](_.y)(SomeTest)

    assertEquals(iso.withDefault(0).get(objSome), 2)
    assertEquals(iso.withDefault(0).get(objNone), 0)

    assertEquals(objNone.optics.andThen(iso).withDefault(0).get, 0)
  }

  test("each") {
    case class SomeTest(y: List[Int])
    val obj = SomeTest(List(1, 2, 3))

    val iso = Iso[SomeTest, List[Int]](_.y)(SomeTest)

    assertEquals(iso.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.optics.andThen(iso).each.getAll, List(1, 2, 3))
  }

  test("filter") {
    case class SomeTest(y: Int)
    val obj = SomeTest(2)

    val iso = Iso[SomeTest, Int](_.y)(SomeTest)

    assertEquals(iso.filter(_ > 0).getOption(obj), Some(2))
    assertEquals(obj.optics.andThen(iso).filter(_ > 0).getOption, Some(2))
  }

  test("filterIndex") {
    case class SomeTest(y: List[String])
    val obj = SomeTest(List("hello", "world"))

    val iso = Iso[SomeTest, List[String]](_.y)(SomeTest)

    assertEquals(iso.filterIndex((_: Int) > 0).getAll(obj), List("world"))
    assertEquals(obj.optics.andThen(iso).filterIndex((_: Int) > 0).getAll, List("world"))
  }

  test("at") {
    val tuple2     = (1, 2)
    val tuple2Lens = Iso.id[(Int, Int)]
    assertEquals(tuple2Lens.at(1).get(tuple2), 1)
    assertEquals(tuple2Lens.at(2).get(tuple2), 2)
    assertEquals(tuple2.optics.andThen(tuple2Lens).at(1).get, 1)
    assertEquals(tuple2.optics.andThen(tuple2Lens).at(2).get, 2)

    val tuple3     = (1, 2, 3)
    val tuple3Lens = Iso.id[(Int, Int, Int)]
    assertEquals(tuple3Lens.at(1).get(tuple3), 1)
    assertEquals(tuple3Lens.at(2).get(tuple3), 2)
    assertEquals(tuple3Lens.at(3).get(tuple3), 3)
    assertEquals(tuple3.optics.andThen(tuple3Lens).at(1).get, 1)
    assertEquals(tuple3.optics.andThen(tuple3Lens).at(2).get, 2)
    assertEquals(tuple3.optics.andThen(tuple3Lens).at(3).get, 3)

    val tuple4     = (1, 2, 3, 4)
    val tuple4Lens = Iso.id[(Int, Int, Int, Int)]
    assertEquals(tuple4Lens.at(1).get(tuple4), 1)
    assertEquals(tuple4Lens.at(2).get(tuple4), 2)
    assertEquals(tuple4Lens.at(3).get(tuple4), 3)
    assertEquals(tuple4Lens.at(4).get(tuple4), 4)
    assertEquals(tuple4.optics.andThen(tuple4Lens).at(1).get, 1)
    assertEquals(tuple4.optics.andThen(tuple4Lens).at(2).get, 2)
    assertEquals(tuple4.optics.andThen(tuple4Lens).at(3).get, 3)
    assertEquals(tuple4.optics.andThen(tuple4Lens).at(4).get, 4)

    val tuple5     = (1, 2, 3, 4, 5)
    val tuple5Lens = Iso.id[(Int, Int, Int, Int, Int)]
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
    val tuple6Lens = Iso.id[(Int, Int, Int, Int, Int, Int)]
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
    val sortedMapLens = Iso.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapLens.at(1).get(sortedMap), Some("one"))
    assertEquals(sortedMapLens.at(2).get(sortedMap), None)
    assertEquals(sortedMap.optics.andThen(sortedMapLens).at(1).get, Some("one"))
    assertEquals(sortedMap.optics.andThen(sortedMapLens).at(2).get, None)

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapLens = Iso.id[immutable.ListMap[Int, String]]
    assertEquals(listMapLens.at(1).get(listMap), Some("one"))
    assertEquals(listMapLens.at(2).get(listMap), None)
    assertEquals(listMap.optics.andThen(listMapLens).at(1).get, Some("one"))
    assertEquals(listMap.optics.andThen(listMapLens).at(2).get, None)

    val map     = immutable.Map(1 -> "one")
    val mapLens = Iso.id[Map[Int, String]]
    assertEquals(mapLens.at(1).get(map), Some("one"))
    assertEquals(mapLens.at(2).get(map), None)
    assertEquals(map.optics.andThen(mapLens).at(1).get, Some("one"))
    assertEquals(map.optics.andThen(mapLens).at(2).get, None)

    val set     = Set(1)
    val setLens = Iso.id[Set[Int]]
    assertEquals(setLens.at(1).get(set), true)
    assertEquals(setLens.at(2).get(set), false)
    assertEquals(set.optics.andThen(setLens).at(1).get, true)
    assertEquals(set.optics.andThen(setLens).at(2).get, false)
  }

  test("index") {
    val list     = List(1)
    val listLens = Iso.id[List[Int]]
    assertEquals(listLens.index(0).getOption(list), Some(1))
    assertEquals(listLens.index(1).getOption(list), None)
    assertEquals(list.optics.andThen(listLens).index(0).getOption, Some(1))
    assertEquals(list.optics.andThen(listLens).index(1).getOption, None)

    val lazyList     = LazyList(1)
    val lazyListLens = Iso.id[LazyList[Int]]
    assertEquals(lazyListLens.index(0).getOption(lazyList), Some(1))
    assertEquals(lazyListLens.index(1).getOption(lazyList), None)
    assertEquals(lazyList.optics.andThen(lazyListLens).index(0).getOption, Some(1))
    assertEquals(lazyList.optics.andThen(lazyListLens).index(1).getOption, None)

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapLens = Iso.id[immutable.ListMap[Int, String]]
    assertEquals(listMapLens.index(0).getOption(listMap), None)
    assertEquals(listMapLens.index(1).getOption(listMap), Some("one"))
    assertEquals(listMap.optics.andThen(listMapLens).index(0).getOption, None)
    assertEquals(listMap.optics.andThen(listMapLens).index(1).getOption, Some("one"))

    val map     = Map(1 -> "one")
    val mapLens = Iso.id[Map[Int, String]]
    assertEquals(mapLens.index(1).getOption(map), Some("one"))
    assertEquals(mapLens.index(0).getOption(map), None)
    assertEquals(map.optics.andThen(mapLens).index(1).getOption, Some("one"))
    assertEquals(map.optics.andThen(mapLens).index(0).getOption, None)

    val sortedMap     = immutable.SortedMap(1 -> "one")
    val sortedMapLens = Iso.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapLens.index(1).getOption(sortedMap), Some("one"))
    assertEquals(sortedMapLens.index(0).getOption(sortedMap), None)
    assertEquals(sortedMap.optics.andThen(sortedMapLens).index(1).getOption, Some("one"))
    assertEquals(sortedMap.optics.andThen(sortedMapLens).index(0).getOption, None)

    val vector     = Vector(1)
    val vectorLens = Iso.id[Vector[Int]]
    assertEquals(vectorLens.index(0).getOption(vector), Some(1))
    assertEquals(vectorLens.index(1).getOption(vector), None)
    assertEquals(vector.optics.andThen(vectorLens).index(0).getOption, Some(1))
    assertEquals(vector.optics.andThen(vectorLens).index(1).getOption, None)

    val chain     = Chain.one(1)
    val chainLens = Iso.id[Chain[Int]]
    assertEquals(chainLens.index(0).getOption(chain), Some(1))
    assertEquals(chainLens.index(1).getOption(chain), None)
    assertEquals(chain.optics.andThen(chainLens).index(0).getOption, Some(1))
    assertEquals(chain.optics.andThen(chainLens).index(1).getOption, None)

    val nec     = NonEmptyChain.one(1)
    val necLens = Iso.id[NonEmptyChain[Int]]
    assertEquals(necLens.index(0).getOption(nec), Some(1))
    assertEquals(necLens.index(1).getOption(nec), None)
    assertEquals(nec.optics.andThen(necLens).index(0).getOption, Some(1))
    assertEquals(nec.optics.andThen(necLens).index(1).getOption, None)

    val nev     = NonEmptyVector.one(1)
    val nevLens = Iso.id[NonEmptyVector[Int]]
    assertEquals(nevLens.index(0).getOption(nev), Some(1))
    assertEquals(nevLens.index(1).getOption(nev), None)
    assertEquals(nev.optics.andThen(nevLens).index(0).getOption, Some(1))
    assertEquals(nev.optics.andThen(nevLens).index(1).getOption, None)

    val nel     = NonEmptyList.one(1)
    val nelLens = Iso.id[NonEmptyList[Int]]
    assertEquals(nelLens.index(0).getOption(nel), Some(1))
    assertEquals(nelLens.index(1).getOption(nel), None)
    assertEquals(nel.optics.andThen(nelLens).index(0).getOption, Some(1))
    assertEquals(nel.optics.andThen(nelLens).index(1).getOption, None)
  }
}
