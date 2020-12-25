package monocle

import monocle.law.discipline.{OptionalTests, PrismTests, SetterTests, TraversalTests}
import monocle.macros.{GenIso, GenPrism}
import cats.arrow.{Category, Compose}
import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}
import cats.syntax.either._

import scala.collection.immutable

class PrismSpec extends MonocleSuite {
  def _right[E, A]: Prism[Either[E, A], A] =
    Prism[Either[E, A], A](_.toOption)(Either.right)
  def _pright[E, A]: Prism[Either[E, A], A] =
    Prism.partial[Either[E, A], A](Function.unlift(_.toOption))(Either.right)

  val _nullary: Prism[Arities, Unit] =
    Prism[Arities, Unit] {
      case Nullary() => Some(())
      case _         => None
    } { case () =>
      Nullary()
    }
  val _unary: Prism[Arities, Int] =
    Prism[Arities, Int] {
      case Unary(i) => Some(i)
      case _        => None
    }(Unary)
  val _binary: Prism[Arities, (String, Int)] =
    Prism[Arities, (String, Int)] {
      case Binary(s, i) => Some((s, i))
      case _            => None
    }(Binary.tupled)
  val _quintary: Prism[Arities, (Char, Boolean, String, Int, Double)] =
    Prism[Arities, (Char, Boolean, String, Int, Double)] {
      case Quintary(c, b, s, i, f) => Some((c, b, s, i, f))
      case _                       => None
    }(Quintary.tupled)

  checkAll("apply Prism", PrismTests(_right[String, Int]))
  checkAll("apply partial Prism", PrismTests(_pright[String, Int]))

  checkAll("prism.asTraversal", OptionalTests(_right[String, Int].asOptional))
  checkAll("prism.asTraversal", TraversalTests(_right[String, Int].asTraversal))
  checkAll("prism.asSetter", SetterTests(_right[String, Int].asSetter))

  checkAll("first", PrismTests(_right[String, Int].first[Boolean]))
  checkAll("second", PrismTests(_right[String, Int].second[Boolean]))
  checkAll("left", PrismTests(_right[String, Int].left[Boolean]))
  checkAll("right", PrismTests(_right[String, Int].right[Boolean]))

  // test implicit resolution of type classes

  test("Prism has a Compose instance") {
    assertEquals(
      Compose[Prism]
        .compose(_right[String, Int], _right[String, Either[String, Int]])
        .getOption(Right(Right(3))),
      Some(3)
    )
  }

  test("Prism has a Category instance") {
    assertEquals(Category[Prism].id[Int].getOption(3), Some(3))
  }

  test("only") {
    assertEquals(Prism.only(5).getOption(5), Some(()))
  }

  test("below") {
    val _5s = Prism.only(5).below[List]
    assertEquals(_5s.getOption(List(1, 2, 3, 4, 5)), None)
    assertEquals(_5s.getOption(List(5, 5, 5)), Some(List((), (), ())))
  }

  test("apply") {
    assertEquals(_nullary(), Nullary())
    assertEquals(_unary(3), Unary(3))
    assertEquals(_binary("foo", 7), Binary("foo", 7))
    assertEquals(_quintary('x', true, "bar", 13, 0.4), Quintary('x', true, "bar", 13, 0.4))
  }

  test("unapply") {
    // format: off
assertEquals(    ((Nullary(): Arities) match { case _nullary(unit) => unit }) ,  (()))
    // format: on
    assertEquals(((Unary(3): Arities) match { case _unary(value) => value * 2 }), 6)
    assertEquals(((Binary("foo", 7): Arities) match { case _binary(s, i) => s + i }), "foo7")
    assertEquals(
      ((Quintary('x', true, "bar", 13, 0.4): Arities) match {
        case _quintary(c, b, s, i, f) => "" + c + b + s + i + f
      }),
      "xtruebar130.4"
    )
  }

  sealed trait IntOrString
  case class I(i: Int)    extends IntOrString
  case class S(s: String) extends IntOrString

  val i = GenPrism[IntOrString, I].andThen(GenIso[I, Int])
  val s = Prism[IntOrString, String] { case S(s) => Some(s); case _ => None }(S.apply)

  test("getOption") {
    assertEquals(i.getOption(I(1)), Some(1))
    assertEquals(i.getOption(S("")), None)

    assertEquals(s.getOption(S("hello")), Some("hello"))
    assertEquals(s.getOption(I(10)), None)
  }

  test("reverseGet") {
    assertEquals(i.reverseGet(3), I(3))
    assertEquals(s.reverseGet("Yop"), S("Yop"))
  }

  test("isEmpty") {
    assertEquals(i.isEmpty(I(1)), false)
    assertEquals(i.isEmpty(S("")), true)
  }

  test("nonEmpty") {
    assertEquals(i.nonEmpty(I(1)), true)
    assertEquals(i.nonEmpty(S("")), false)
  }

  test("find") {
    assertEquals(i.find(_ > 5)(I(9)), Some(9))
    assertEquals(i.find(_ > 5)(I(2)), None)
  }

  test("exist") {
    assertEquals(i.exist(_ > 5)(I(9)), true)
    assertEquals(i.exist(_ > 5)(I(2)), false)
    assertEquals(i.exist(_ > 5)(S("")), false)
  }

  test("all") {
    assertEquals(i.all(_ > 5)(I(9)), true)
    assertEquals(i.all(_ > 5)(I(2)), false)
    assertEquals(i.all(_ > 5)(S("")), true)
  }

  test("modify") {
    assertEquals(i.modify(_ + 1)(I(3)), I(4))
    assertEquals(i.modify(_ + 1)(S("")), S(""))
  }

  test("modifyOption") {
    assertEquals(i.modifyOption(_ + 1)(I(3)), Some(I(4)))
    assertEquals(i.modifyOption(_ + 1)(S("")), None)
  }

  test("set") {
    assertEquals(i.replace(1)(I(3)), I(1))
    assertEquals(i.replace(1)(S("")), S(""))
  }

  test("setOption") {
    assertEquals(i.setOption(1)(I(3)), Some(I(1)))
    assertEquals(i.setOption(1)(S("")), None)
  }

  test("GenPrism nullary equality".ignore) {
    assertEquals(
      GenPrism[Arities, Nullary].andThen(GenIso.unit[Nullary]),
      _nullary
    )
  }

  test("GenPrism unary equality".ignore) {
    assertEquals(GenPrism[Arities, Unary].andThen(GenIso[Unary, Int]), _unary)
  }

  test("GenPrism binary equality".ignore) {
    assertEquals(GenPrism[Arities, Binary].andThen(GenIso.fields[Binary]), _binary)
  }

  test("GenPrism quintary equality".ignore) {
    assertEquals(GenPrism[Arities, Quintary].andThen(GenIso.fields[Quintary]), _quintary)
  }

  test("to") {
    assertEquals(i.to(_.toString()).getAll(I(1)), List("1"))
  }

  test("some") {
    case class SomeTest(y: Option[Int])
    val obj = SomeTest(Some(2))

    val prism = Iso[SomeTest, Option[Int]](_.y)(SomeTest).asPrism

    assertEquals(prism.some.getOption(obj), Some(2))
    assertEquals(obj.optics.andThen(prism).some.getOption, Some(2))
  }

  test("withDefault") {
    case class SomeTest(y: Option[Int])
    val objSome = SomeTest(Some(2))
    val objNone = SomeTest(None)

    val prism = Iso[SomeTest, Option[Int]](_.y)(SomeTest).asPrism

    assertEquals(prism.withDefault(0).getOption(objSome), Some(2))
    assertEquals(prism.withDefault(0).getOption(objNone), Some(0))

    assertEquals(objNone.optics.andThen(prism).withDefault(0).getOption, Some(0))
  }

  test("each") {
    case class SomeTest(y: List[Int])
    val obj = SomeTest(List(1, 2, 3))

    val prism = Iso[SomeTest, List[Int]](_.y)(SomeTest).asPrism

    assertEquals(prism.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.optics.andThen(prism).each.getAll, List(1, 2, 3))
  }

  test("filter") {
    case class SomeTest(y: Int)
    val obj = SomeTest(2)

    val prism = Iso[SomeTest, Int](_.y)(SomeTest).asPrism

    assertEquals(prism.filter(_ > 0).getOption(obj), Some(2))
    assertEquals(obj.optics.andThen(prism).filter(_ > 0).getOption, Some(2))
  }

  test("filterIndex") {
    case class SomeTest(y: List[String])
    val obj = SomeTest(List("hello", "world"))

    val prism = Iso[SomeTest, List[String]](_.y)(SomeTest).asPrism

    assertEquals(prism.filterIndex((_: Int) > 0).getAll(obj), List("world"))
    assertEquals(obj.optics.andThen(prism).filterIndex((_: Int) > 0).getAll, List("world"))
  }

  test("at") {
    val tuple2      = (1, 2)
    val tuple2Prism = Prism.id[(Int, Int)]
    assertEquals(tuple2Prism.at(1).getOption(tuple2), Some(1))
    assertEquals(tuple2Prism.at(2).getOption(tuple2), Some(2))
    assertEquals(tuple2.optics.andThen(tuple2Prism).at(1).getOption, Some(1))
    assertEquals(tuple2.optics.andThen(tuple2Prism).at(2).getOption, Some(2))

    val tuple3      = (1, 2, 3)
    val tuple3Prism = Prism.id[(Int, Int, Int)]
    assertEquals(tuple3Prism.at(1).getOption(tuple3), Some(1))
    assertEquals(tuple3Prism.at(2).getOption(tuple3), Some(2))
    assertEquals(tuple3Prism.at(3).getOption(tuple3), Some(3))
    assertEquals(tuple3.optics.andThen(tuple3Prism).at(1).getOption, Some(1))
    assertEquals(tuple3.optics.andThen(tuple3Prism).at(2).getOption, Some(2))
    assertEquals(tuple3.optics.andThen(tuple3Prism).at(3).getOption, Some(3))

    val tuple4      = (1, 2, 3, 4)
    val tuple4Prism = Prism.id[(Int, Int, Int, Int)]
    assertEquals(tuple4Prism.at(1).getOption(tuple4), Some(1))
    assertEquals(tuple4Prism.at(2).getOption(tuple4), Some(2))
    assertEquals(tuple4Prism.at(3).getOption(tuple4), Some(3))
    assertEquals(tuple4Prism.at(4).getOption(tuple4), Some(4))
    assertEquals(tuple4.optics.andThen(tuple4Prism).at(1).getOption, Some(1))
    assertEquals(tuple4.optics.andThen(tuple4Prism).at(2).getOption, Some(2))
    assertEquals(tuple4.optics.andThen(tuple4Prism).at(3).getOption, Some(3))
    assertEquals(tuple4.optics.andThen(tuple4Prism).at(4).getOption, Some(4))

    val tuple5      = (1, 2, 3, 4, 5)
    val tuple5Prism = Prism.id[(Int, Int, Int, Int, Int)]
    assertEquals(tuple5Prism.at(1).getOption(tuple5), Some(1))
    assertEquals(tuple5Prism.at(2).getOption(tuple5), Some(2))
    assertEquals(tuple5Prism.at(3).getOption(tuple5), Some(3))
    assertEquals(tuple5Prism.at(4).getOption(tuple5), Some(4))
    assertEquals(tuple5Prism.at(5).getOption(tuple5), Some(5))
    assertEquals(tuple5.optics.andThen(tuple5Prism).at(1).getOption, Some(1))
    assertEquals(tuple5.optics.andThen(tuple5Prism).at(2).getOption, Some(2))
    assertEquals(tuple5.optics.andThen(tuple5Prism).at(3).getOption, Some(3))
    assertEquals(tuple5.optics.andThen(tuple5Prism).at(4).getOption, Some(4))
    assertEquals(tuple5.optics.andThen(tuple5Prism).at(5).getOption, Some(5))

    val tuple6      = (1, 2, 3, 4, 5, 6)
    val tuple6Prism = Prism.id[(Int, Int, Int, Int, Int, Int)]
    assertEquals(tuple6Prism.at(1).getOption(tuple6), Some(1))
    assertEquals(tuple6Prism.at(2).getOption(tuple6), Some(2))
    assertEquals(tuple6Prism.at(3).getOption(tuple6), Some(3))
    assertEquals(tuple6Prism.at(4).getOption(tuple6), Some(4))
    assertEquals(tuple6Prism.at(5).getOption(tuple6), Some(5))
    assertEquals(tuple6Prism.at(6).getOption(tuple6), Some(6))
    assertEquals(tuple6.optics.andThen(tuple6Prism).at(1).getOption, Some(1))
    assertEquals(tuple6.optics.andThen(tuple6Prism).at(2).getOption, Some(2))
    assertEquals(tuple6.optics.andThen(tuple6Prism).at(3).getOption, Some(3))
    assertEquals(tuple6.optics.andThen(tuple6Prism).at(4).getOption, Some(4))
    assertEquals(tuple6.optics.andThen(tuple6Prism).at(5).getOption, Some(5))
    assertEquals(tuple6.optics.andThen(tuple6Prism).at(6).getOption, Some(6))

    val sortedMap      = immutable.SortedMap(1 -> "one")
    val sortedMapPrism = Prism.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapPrism.at(1).getOption(sortedMap), Some(Some("one")))
    assertEquals(sortedMapPrism.at(0).getOption(sortedMap), Some(None))
    assertEquals(sortedMap.optics.andThen(sortedMapPrism).at(1).getOption, Some(Some("one")))
    assertEquals(sortedMap.optics.andThen(sortedMapPrism).at(0).getOption, Some(None))

    val listMap      = immutable.ListMap(1 -> "one")
    val listMapPrism = Prism.id[immutable.ListMap[Int, String]]
    assertEquals(listMapPrism.at(1).getOption(listMap), Some(Some("one")))
    assertEquals(listMapPrism.at(0).getOption(listMap), Some(None))
    assertEquals(listMap.optics.andThen(listMapPrism).at(1).getOption, Some(Some("one")))
    assertEquals(listMap.optics.andThen(listMapPrism).at(0).getOption, Some(None))

    val map      = immutable.Map(1 -> "one")
    val mapPrism = Prism.id[Map[Int, String]]
    assertEquals(mapPrism.at(1).getOption(map), Some(Some("one")))
    assertEquals(mapPrism.at(0).getOption(map), Some(None))
    assertEquals(map.optics.andThen(mapPrism).at(1).getOption, Some(Some("one")))
    assertEquals(map.optics.andThen(mapPrism).at(0).getOption, Some(None))

    val set      = Set(1)
    val setPrism = Prism.id[Set[Int]]
    assertEquals(setPrism.at(1).getOption(set), Some(true))
    assertEquals(setPrism.at(0).getOption(set), Some(false))
    assertEquals(set.optics.andThen(setPrism).at(1).getOption, Some(true))
    assertEquals(set.optics.andThen(setPrism).at(0).getOption, Some(false))
  }

  test("index") {
    val list      = List(1)
    val listPrism = Prism.id[List[Int]]
    assertEquals(listPrism.index(0).getOption(list), Some(1))
    assertEquals(listPrism.index(1).getOption(list), None)
    assertEquals(list.optics.andThen(listPrism).index(0).getOption, Some(1))
    assertEquals(list.optics.andThen(listPrism).index(1).getOption, None)

    val lazyList      = LazyList(1)
    val lazyListPrism = Prism.id[LazyList[Int]]
    assertEquals(lazyListPrism.index(0).getOption(lazyList), Some(1))
    assertEquals(lazyListPrism.index(1).getOption(lazyList), None)
    assertEquals(lazyList.optics.andThen(lazyListPrism).index(0).getOption, Some(1))
    assertEquals(lazyList.optics.andThen(lazyListPrism).index(1).getOption, None)

    val listMap      = immutable.ListMap(1 -> "one")
    val listMapPrism = Prism.id[immutable.ListMap[Int, String]]
    assertEquals(listMapPrism.index(0).getOption(listMap), None)
    assertEquals(listMapPrism.index(1).getOption(listMap), Some("one"))
    assertEquals(listMap.optics.andThen(listMapPrism).index(0).getOption, None)
    assertEquals(listMap.optics.andThen(listMapPrism).index(1).getOption, Some("one"))

    val map      = Map(1 -> "one")
    val mapPrism = Prism.id[Map[Int, String]]
    assertEquals(mapPrism.index(0).getOption(map), None)
    assertEquals(mapPrism.index(1).getOption(map), Some("one"))
    assertEquals(map.optics.andThen(mapPrism).index(0).getOption, None)
    assertEquals(map.optics.andThen(mapPrism).index(1).getOption, Some("one"))

    val sortedMap      = immutable.SortedMap(1 -> "one")
    val sortedMapPrism = Prism.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapPrism.index(0).getOption(sortedMap), None)
    assertEquals(sortedMapPrism.index(1).getOption(sortedMap), Some("one"))
    assertEquals(sortedMap.optics.andThen(sortedMapPrism).index(0).getOption, None)
    assertEquals(sortedMap.optics.andThen(sortedMapPrism).index(1).getOption, Some("one"))

    val vector      = Vector(1)
    val vectorPrism = Prism.id[Vector[Int]]
    assertEquals(vectorPrism.index(0).getOption(vector), Some(1))
    assertEquals(vectorPrism.index(1).getOption(vector), None)
    assertEquals(vector.optics.andThen(vectorPrism).index(0).getOption, Some(1))
    assertEquals(vector.optics.andThen(vectorPrism).index(1).getOption, None)

    val chain      = Chain.one(1)
    val chainPrism = Prism.id[Chain[Int]]
    assertEquals(chainPrism.index(0).getOption(chain), Some(1))
    assertEquals(chainPrism.index(1).getOption(chain), None)
    assertEquals(chain.optics.andThen(chainPrism).index(0).getOption, Some(1))
    assertEquals(chain.optics.andThen(chainPrism).index(1).getOption, None)

    val nec      = NonEmptyChain.one(1)
    val necPrism = Prism.id[NonEmptyChain[Int]]
    assertEquals(necPrism.index(0).getOption(nec), Some(1))
    assertEquals(necPrism.index(1).getOption(nec), None)
    assertEquals(nec.optics.andThen(necPrism).index(0).getOption, Some(1))
    assertEquals(nec.optics.andThen(necPrism).index(1).getOption, None)

    val nev      = NonEmptyVector.one(1)
    val nevPrism = Prism.id[NonEmptyVector[Int]]
    assertEquals(nevPrism.index(0).getOption(nev), Some(1))
    assertEquals(nevPrism.index(1).getOption(nev), None)
    assertEquals(nev.optics.andThen(nevPrism).index(0).getOption, Some(1))
    assertEquals(nev.optics.andThen(nevPrism).index(1).getOption, None)

    val nel      = NonEmptyList.one(1)
    val nelPrism = Prism.id[NonEmptyList[Int]]
    assertEquals(nelPrism.index(0).getOption(nel), Some(1))
    assertEquals(nelPrism.index(1).getOption(nel), None)
    assertEquals(nel.optics.andThen(nelPrism).index(0).getOption, Some(1))
    assertEquals(nel.optics.andThen(nelPrism).index(1).getOption, None)
  }
}
