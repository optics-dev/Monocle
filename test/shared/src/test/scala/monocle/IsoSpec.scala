package monocle

import monocle.law.discipline._
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
  val _unary: Iso[Unary, Int] = Iso[Unary, Int](_.i)(Unary.apply)
  val _binary: Iso[Binary, (String, Int)] =
    Iso[Binary, (String, Int)](b => (b.s, b.i))(Binary.apply.tupled)
  val _quintary: Iso[Quintary, (Char, Boolean, String, Int, Double)] =
    Iso[Quintary, (Char, Boolean, String, Int, Double)](b => (b.c, b.b, b.s, b.i, b.f))(Quintary.apply.tupled)

  case class IntWrapper(i: Int)
  implicit val intWrapperGen: Arbitrary[IntWrapper] = Arbitrary(arbitrary[Int].map(IntWrapper.apply))
  implicit val intWrapperEq: Eq[IntWrapper]         = Eq.fromUniversalEquals[IntWrapper]

  case class IdWrapper[A](value: A)
  implicit def idWrapperGen[A: Arbitrary]: Arbitrary[IdWrapper[A]] =
    Arbitrary(arbitrary[A].map(IdWrapper.apply))
  implicit def idWrapperEq[A: Eq]: Eq[IdWrapper[A]] = Eq.fromUniversalEquals

  case class EmptyCase()
  implicit val emptyCaseGen: Arbitrary[EmptyCase] = Arbitrary(Gen.const(EmptyCase()))
  implicit val emptyCaseEq: Eq[EmptyCase]         = Eq.fromUniversalEquals[EmptyCase]

  case class EmptyCaseType[A]()
  implicit def emptyCaseTypeGen[A]: Arbitrary[EmptyCaseType[A]] =
    Arbitrary(Gen.const(EmptyCaseType()))
  implicit def emptyCaseTypeEq[A]: Eq[EmptyCaseType[A]] = Eq.fromUniversalEquals[EmptyCaseType[A]]

  val iso = Iso[IntWrapper, Int](_.i)(IntWrapper.apply)
  val involutedListReverse =
    Iso.involuted[List[Int]](_.reverse) // ∀ {T} -> List(ts: T*).reverse.reverse == List(ts: T*)
  val involutedTwoMinusN = Iso.involuted[Int](2 - _) //  ∀ {n : Int} -> n == 2 - (2 - n)

  checkAll("apply Iso", IsoTests(iso))
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
    assertEquals(Unary(3) match { case _unary(value) => value * 2 }, 6)
    assertEquals(Binary("foo", 7) match { case _binary(s, i) => s + i }, "foo7")
    assertEquals(
      Quintary('x', true, "bar", 13, 0.4) match {
        case _quintary(c, b, s, i, f) => "" + c + b + s + i + f
      },
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

  test("to") {
    assertEquals(iso.to(_.toString()).get(IntWrapper(5)), "5")
  }

  test("some") {
    case class SomeTest(y: Option[Int])
    val obj = SomeTest(Some(2))

    val iso = Iso[SomeTest, Option[Int]](_.y)(SomeTest.apply)

    assertEquals(iso.some.getOption(obj), Some(2))
    assertEquals(obj.focus().andThen(iso).some.getOption, Some(2))
  }

  test("withDefault") {
    case class SomeTest(y: Option[Int])
    val objSome = SomeTest(Some(2))
    val objNone = SomeTest(None)

    val iso = Iso[SomeTest, Option[Int]](_.y)(SomeTest.apply)

    assertEquals(iso.withDefault(0).get(objSome), 2)
    assertEquals(iso.withDefault(0).get(objNone), 0)

    assertEquals(objNone.focus().andThen(iso).withDefault(0).get, 0)
  }

  test("each") {
    case class SomeTest(y: List[Int])
    val obj = SomeTest(List(1, 2, 3))

    val iso = Iso[SomeTest, List[Int]](_.y)(SomeTest.apply)

    assertEquals(iso.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.focus().andThen(iso).each.getAll, List(1, 2, 3))
  }

  test("filter") {
    case class SomeTest(y: Int)
    val obj = SomeTest(2)

    val iso = Iso[SomeTest, Int](_.y)(SomeTest.apply)

    assertEquals(iso.filter(_ > 0).getOption(obj), Some(2))
    assertEquals(obj.focus().andThen(iso).filter(_ > 0).getOption, Some(2))
  }

  test("filterIndex") {
    case class SomeTest(y: List[String])
    val obj = SomeTest(List("hello", "world"))

    val iso = Iso[SomeTest, List[String]](_.y)(SomeTest.apply)

    assertEquals(iso.filterIndex((_: Int) > 0).getAll(obj), List("world"))
    assertEquals(obj.focus().andThen(iso).filterIndex((_: Int) > 0).getAll, List("world"))
  }

  test("at") {
    val sortedMap     = immutable.SortedMap(1 -> "one")
    val sortedMapLens = Iso.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapLens.at(1).get(sortedMap), Some("one"))
    assertEquals(sortedMapLens.at(2).get(sortedMap), None)
    assertEquals(sortedMap.focus().andThen(sortedMapLens).at(1).get, Some("one"))
    assertEquals(sortedMap.focus().andThen(sortedMapLens).at(2).get, None)

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapLens = Iso.id[immutable.ListMap[Int, String]]
    assertEquals(listMapLens.at(1).get(listMap), Some("one"))
    assertEquals(listMapLens.at(2).get(listMap), None)
    assertEquals(listMap.focus().andThen(listMapLens).at(1).get, Some("one"))
    assertEquals(listMap.focus().andThen(listMapLens).at(2).get, None)

    val map     = immutable.Map(1 -> "one")
    val mapLens = Iso.id[Map[Int, String]]
    assertEquals(mapLens.at(1).get(map), Some("one"))
    assertEquals(mapLens.at(2).get(map), None)
    assertEquals(map.focus().andThen(mapLens).at(1).get, Some("one"))
    assertEquals(map.focus().andThen(mapLens).at(2).get, None)

    val set     = Set(1)
    val setLens = Iso.id[Set[Int]]
    assertEquals(setLens.at(1).get(set), true)
    assertEquals(setLens.at(2).get(set), false)
    assertEquals(set.focus().andThen(setLens).at(1).get, true)
    assertEquals(set.focus().andThen(setLens).at(2).get, false)
  }

  test("index") {
    val list     = List(1)
    val listLens = Iso.id[List[Int]]
    assertEquals(listLens.index(0).getOption(list), Some(1))
    assertEquals(listLens.index(1).getOption(list), None)
    assertEquals(list.focus().andThen(listLens).index(0).getOption, Some(1))
    assertEquals(list.focus().andThen(listLens).index(1).getOption, None)

    val lazyList     = LazyList(1)
    val lazyListLens = Iso.id[LazyList[Int]]
    assertEquals(lazyListLens.index(0).getOption(lazyList), Some(1))
    assertEquals(lazyListLens.index(1).getOption(lazyList), None)
    assertEquals(lazyList.focus().andThen(lazyListLens).index(0).getOption, Some(1))
    assertEquals(lazyList.focus().andThen(lazyListLens).index(1).getOption, None)

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapLens = Iso.id[immutable.ListMap[Int, String]]
    assertEquals(listMapLens.index(0).getOption(listMap), None)
    assertEquals(listMapLens.index(1).getOption(listMap), Some("one"))
    assertEquals(listMap.focus().andThen(listMapLens).index(0).getOption, None)
    assertEquals(listMap.focus().andThen(listMapLens).index(1).getOption, Some("one"))

    val map     = Map(1 -> "one")
    val mapLens = Iso.id[Map[Int, String]]
    assertEquals(mapLens.index(1).getOption(map), Some("one"))
    assertEquals(mapLens.index(0).getOption(map), None)
    assertEquals(map.focus().andThen(mapLens).index(1).getOption, Some("one"))
    assertEquals(map.focus().andThen(mapLens).index(0).getOption, None)

    val sortedMap     = immutable.SortedMap(1 -> "one")
    val sortedMapLens = Iso.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapLens.index(1).getOption(sortedMap), Some("one"))
    assertEquals(sortedMapLens.index(0).getOption(sortedMap), None)
    assertEquals(sortedMap.focus().andThen(sortedMapLens).index(1).getOption, Some("one"))
    assertEquals(sortedMap.focus().andThen(sortedMapLens).index(0).getOption, None)

    val vector     = Vector(1)
    val vectorLens = Iso.id[Vector[Int]]
    assertEquals(vectorLens.index(0).getOption(vector), Some(1))
    assertEquals(vectorLens.index(1).getOption(vector), None)
    assertEquals(vector.focus().andThen(vectorLens).index(0).getOption, Some(1))
    assertEquals(vector.focus().andThen(vectorLens).index(1).getOption, None)

    val chain     = Chain.one(1)
    val chainLens = Iso.id[Chain[Int]]
    assertEquals(chainLens.index(0).getOption(chain), Some(1))
    assertEquals(chainLens.index(1).getOption(chain), None)
    assertEquals(chain.focus().andThen(chainLens).index(0).getOption, Some(1))
    assertEquals(chain.focus().andThen(chainLens).index(1).getOption, None)

    val nec     = NonEmptyChain.one(1)
    val necLens = Iso.id[NonEmptyChain[Int]]
    assertEquals(necLens.index(0).getOption(nec), Some(1))
    assertEquals(necLens.index(1).getOption(nec), None)
    assertEquals(nec.focus().andThen(necLens).index(0).getOption, Some(1))
    assertEquals(nec.focus().andThen(necLens).index(1).getOption, None)

    val nev     = NonEmptyVector.one(1)
    val nevLens = Iso.id[NonEmptyVector[Int]]
    assertEquals(nevLens.index(0).getOption(nev), Some(1))
    assertEquals(nevLens.index(1).getOption(nev), None)
    assertEquals(nev.focus().andThen(nevLens).index(0).getOption, Some(1))
    assertEquals(nev.focus().andThen(nevLens).index(1).getOption, None)

    val nel     = NonEmptyList.one(1)
    val nelLens = Iso.id[NonEmptyList[Int]]
    assertEquals(nelLens.index(0).getOption(nel), Some(1))
    assertEquals(nelLens.index(1).getOption(nel), None)
    assertEquals(nel.focus().andThen(nelLens).index(0).getOption, Some(1))
    assertEquals(nel.focus().andThen(nelLens).index(1).getOption, None)
  }
}
