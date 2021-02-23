package monocle

import cats.Monoid
import cats.arrow.{Category, Choice, Compose}
import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}

import scala.collection.immutable

class FoldSpec extends MonocleSuite {
  val eachLi: Fold[List[Int], Int]             = Fold.fromFoldable[List, Int]
  def eachL2[A, B]: Fold[List[(A, B)], (A, B)] = Fold.fromFoldable[List, (A, B)]

  def nestedListFold[A] =
    new Fold[List[List[A]], List[A]] {
      def foldMap[M: Monoid](f: (List[A]) => M)(s: List[List[A]]): M =
        s.foldRight(Monoid[M].empty)((l, acc) => Monoid[M].combine(f(l), acc))
    }

  // test implicit resolution of type classes

  test("Fold has a Compose instance") {
    assertEquals(
      Compose[Fold]
        .compose(eachLi, nestedListFold[Int])
        .fold(List(List(1, 2, 3), List(4, 5), List(6))),
      21
    )
  }

  test("Fold has a Category instance") {
    assertEquals(Category[Fold].id[Int].fold(3), 3)
  }

  test("Fold has a Choice instance") {
    assertEquals(
      Choice[Fold]
        .choice(eachLi, Choice[Fold].id[Int])
        .fold(Left(List(1, 2, 3))),
      6
    )
  }

  test("foldMap") {
    assertEquals(eachLi.foldMap(_.toString)(List(1, 2, 3, 4, 5)), "12345")
  }

  test("getAll") {
    assertEquals(eachLi.getAll(List(1, 2, 3, 4)), List(1, 2, 3, 4))
  }

  test("headOption") {
    assertEquals(eachLi.headOption(List(1, 2, 3, 4)), Some(1))
  }

  test("lastOption") {
    assertEquals(eachLi.lastOption(List(1, 2, 3, 4)), Some(4))
  }

  test("length") {
    assertEquals(eachLi.length(List(1, 2, 3, 4)), 4)
    assertEquals(eachLi.length(Nil), 0)
  }

  test("isEmpty") {
    assertEquals(eachLi.isEmpty(List(1, 2, 3, 4)), false)
    assertEquals(eachLi.isEmpty(Nil), true)
  }

  test("nonEmpty") {
    assertEquals(eachLi.nonEmpty(List(1, 2, 3, 4)), true)
    assertEquals(eachLi.nonEmpty(Nil), false)
  }

  test("find") {
    assertEquals(eachLi.find(_ > 2)(List(1, 2, 3, 4)), Some(3))
    assertEquals(eachLi.find(_ > 9)(List(1, 2, 3, 4)), None)
  }

  test("exist") {
    assertEquals(eachLi.exist(_ > 2)(List(1, 2, 3, 4)), true)
    assertEquals(eachLi.exist(_ > 9)(List(1, 2, 3, 4)), false)
    assertEquals(eachLi.exist(_ > 9)(Nil), false)
  }

  test("all") {
    assertEquals(eachLi.all(_ > 2)(List(1, 2, 3, 4)), false)
    assertEquals(eachLi.all(_ > 0)(List(1, 2, 3, 4)), true)
    assertEquals(eachLi.all(_ > 0)(Nil), true)
  }

  test("select (satisfied predicate)") {
    val select = Fold.select[List[Int]](_.endsWith(List(2, 3)))
    assertEquals(select.getAll(List(1, 2, 3)), List(List(1, 2, 3)))
  }

  test("select (unsatisfied predicate)") {
    val select = Fold.select[List[Int]](_.endsWith(List(2, 3)))
    assertEquals(select.getAll(List(1, 2, 3, 4)), List())
  }

  test("to") {
    assertEquals(eachLi.to(_.toString()).getAll(List(1, 2, 3)), List("1", "2", "3"))
  }

  test("some") {
    val numbers = List(Some(1), None, Some(2), None)
    val fold    = Fold.fromFoldable[List, Option[Int]]

    assertEquals(fold.some.getAll(numbers), List(1, 2))
    assertEquals(numbers.optics.andThen(fold).some.getAll, List(1, 2))
  }

  test("withDefault") {
    val numbers = List(Some(1), None, Some(2), None)
    val fold    = Fold.fromFoldable[List, Option[Int]]

    assertEquals(fold.withDefault(0).getAll(numbers), List(1, 0, 2, 0))
    assertEquals(numbers.optics.andThen(fold).withDefault(0).getAll, List(1, 0, 2, 0))
  }

  test("each") {
    val numbers = List(List(1, 2, 3), Nil, List(4), Nil)
    val fold    = Fold.fromFoldable[List, List[Int]]

    assertEquals(fold.each.getAll(numbers), List(1, 2, 3, 4))
    assertEquals(numbers.optics.andThen(fold).each.getAll, List(1, 2, 3, 4))
  }

  test("filter") {
    val numbers = List(1, 2, 3)
    val fold    = Fold.fromFoldable[List, Int]

    assertEquals(fold.filter(_ > 1).getAll(numbers), List(2, 3))
    assertEquals(numbers.optics.andThen(fold).filter(_ > 1).getAll, List(2, 3))
  }

  test("filterIndex") {
    val words = List(List("hello", "world"), List("hey", "hi"))
    val fold  = Fold.fromFoldable[List, List[String]]

    assertEquals(fold.filterIndex((_: Int) > 0).getAll(words), List("world", "hi"))
    assertEquals(words.optics.andThen(fold).filterIndex((_: Int) > 0).getAll, List("world", "hi"))
  }

  test("at") {
    val tuple2     = (1, 2)
    val tuple2Fold = Iso.id[(Int, Int)].asFold
    assertEquals(tuple2Fold.at(1).getAll(tuple2), List(1))
    assertEquals(tuple2Fold.at(2).getAll(tuple2), List(2))
    assertEquals(tuple2.optics.andThen(tuple2Fold).at(1).getAll, List(1))
    assertEquals(tuple2.optics.andThen(tuple2Fold).at(2).getAll, List(2))

    val tuple3     = (1, 2, 3)
    val tuple3Fold = Iso.id[(Int, Int, Int)].asFold
    assertEquals(tuple3Fold.at(1).getAll(tuple3), List(1))
    assertEquals(tuple3Fold.at(2).getAll(tuple3), List(2))
    assertEquals(tuple3Fold.at(3).getAll(tuple3), List(3))
    assertEquals(tuple3.optics.andThen(tuple3Fold).at(1).getAll, List(1))
    assertEquals(tuple3.optics.andThen(tuple3Fold).at(2).getAll, List(2))
    assertEquals(tuple3.optics.andThen(tuple3Fold).at(3).getAll, List(3))

    val tuple4     = (1, 2, 3, 4)
    val tuple4Fold = Iso.id[(Int, Int, Int, Int)].asFold
    assertEquals(tuple4Fold.at(1).getAll(tuple4), List(1))
    assertEquals(tuple4Fold.at(2).getAll(tuple4), List(2))
    assertEquals(tuple4Fold.at(3).getAll(tuple4), List(3))
    assertEquals(tuple4Fold.at(4).getAll(tuple4), List(4))
    assertEquals(tuple4.optics.andThen(tuple4Fold).at(1).getAll, List(1))
    assertEquals(tuple4.optics.andThen(tuple4Fold).at(2).getAll, List(2))
    assertEquals(tuple4.optics.andThen(tuple4Fold).at(3).getAll, List(3))
    assertEquals(tuple4.optics.andThen(tuple4Fold).at(4).getAll, List(4))

    val tuple5     = (1, 2, 3, 4, 5)
    val tuple5Fold = Iso.id[(Int, Int, Int, Int, Int)].asFold
    assertEquals(tuple5Fold.at(1).getAll(tuple5), List(1))
    assertEquals(tuple5Fold.at(2).getAll(tuple5), List(2))
    assertEquals(tuple5Fold.at(3).getAll(tuple5), List(3))
    assertEquals(tuple5Fold.at(4).getAll(tuple5), List(4))
    assertEquals(tuple5Fold.at(5).getAll(tuple5), List(5))
    assertEquals(tuple5.optics.andThen(tuple5Fold).at(1).getAll, List(1))
    assertEquals(tuple5.optics.andThen(tuple5Fold).at(2).getAll, List(2))
    assertEquals(tuple5.optics.andThen(tuple5Fold).at(3).getAll, List(3))
    assertEquals(tuple5.optics.andThen(tuple5Fold).at(4).getAll, List(4))
    assertEquals(tuple5.optics.andThen(tuple5Fold).at(5).getAll, List(5))

    val tuple6     = (1, 2, 3, 4, 5, 6)
    val tuple6Fold = Iso.id[(Int, Int, Int, Int, Int, Int)].asFold
    assertEquals(tuple6Fold.at(1).getAll(tuple6), List(1))
    assertEquals(tuple6Fold.at(2).getAll(tuple6), List(2))
    assertEquals(tuple6Fold.at(3).getAll(tuple6), List(3))
    assertEquals(tuple6Fold.at(4).getAll(tuple6), List(4))
    assertEquals(tuple6Fold.at(5).getAll(tuple6), List(5))
    assertEquals(tuple6Fold.at(6).getAll(tuple6), List(6))
    assertEquals(tuple6.optics.andThen(tuple6Fold).at(1).getAll, List(1))
    assertEquals(tuple6.optics.andThen(tuple6Fold).at(2).getAll, List(2))
    assertEquals(tuple6.optics.andThen(tuple6Fold).at(3).getAll, List(3))
    assertEquals(tuple6.optics.andThen(tuple6Fold).at(4).getAll, List(4))
    assertEquals(tuple6.optics.andThen(tuple6Fold).at(5).getAll, List(5))
    assertEquals(tuple6.optics.andThen(tuple6Fold).at(6).getAll, List(6))

    val sortedMap     = immutable.SortedMap(1 -> "one")
    val sortedMapFold = Iso.id[immutable.SortedMap[Int, String]].asFold
    assertEquals(sortedMapFold.at(1).getAll(sortedMap), List(Some("one")))
    assertEquals(sortedMapFold.at(0).getAll(sortedMap), List(None))
    assertEquals(sortedMap.optics.andThen(sortedMapFold).at(1).getAll, List(Some("one")))
    assertEquals(sortedMap.optics.andThen(sortedMapFold).at(0).getAll, List(None))

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapFold = Iso.id[immutable.ListMap[Int, String]].asFold
    assertEquals(listMapFold.at(1).getAll(listMap), List(Some("one")))
    assertEquals(listMapFold.at(0).getAll(listMap), List(None))
    assertEquals(listMap.optics.andThen(listMapFold).at(1).getAll, List(Some("one")))
    assertEquals(listMap.optics.andThen(listMapFold).at(0).getAll, List(None))

    val map     = immutable.Map(1 -> "one")
    val mapFold = Iso.id[Map[Int, String]].asFold
    assertEquals(mapFold.at(1).getAll(map), List(Some("one")))
    assertEquals(mapFold.at(0).getAll(map), List(None))
    assertEquals(map.optics.andThen(mapFold).at(1).getAll, List(Some("one")))
    assertEquals(map.optics.andThen(mapFold).at(0).getAll, List(None))

    val set     = Set(1)
    val setFold = Iso.id[Set[Int]].asFold
    assertEquals(setFold.at(1).getAll(set), List(true))
    assertEquals(setFold.at(0).getAll(set), List(false))
    assertEquals(set.optics.andThen(setFold).at(1).getAll, List(true))
    assertEquals(set.optics.andThen(setFold).at(0).getAll, List(false))
  }

  test("index") {
    val list     = List(1)
    val listFold = Iso.id[List[Int]].asFold
    assertEquals(listFold.index(0).getAll(list), List(1))
    assertEquals(listFold.index(1).getAll(list), Nil)
    assertEquals(list.optics.andThen(listFold).index(0).getAll, List(1))
    assertEquals(list.optics.andThen(listFold).index(1).getAll, Nil)

    val lazyList     = LazyList(1)
    val lazyListFold = Iso.id[LazyList[Int]].asFold
    assertEquals(lazyListFold.index(0).getAll(lazyList), List(1))
    assertEquals(lazyListFold.index(1).getAll(lazyList), Nil)
    assertEquals(lazyList.optics.andThen(lazyListFold).index(0).getAll, List(1))
    assertEquals(lazyList.optics.andThen(lazyListFold).index(1).getAll, Nil)

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapFold = Iso.id[immutable.ListMap[Int, String]].asFold
    assertEquals(listMapFold.index(0).getAll(listMap), Nil)
    assertEquals(listMapFold.index(1).getAll(listMap), List("one"))
    assertEquals(listMap.optics.andThen(listMapFold).index(0).getAll, Nil)
    assertEquals(listMap.optics.andThen(listMapFold).index(1).getAll, List("one"))

    val map     = Map(1 -> "one")
    val mapFold = Iso.id[Map[Int, String]].asFold
    assertEquals(mapFold.index(0).getAll(map), Nil)
    assertEquals(mapFold.index(1).getAll(map), List("one"))
    assertEquals(map.optics.andThen(mapFold).index(0).getAll, Nil)
    assertEquals(map.optics.andThen(mapFold).index(1).getAll, List("one"))

    val sortedMap     = immutable.SortedMap(1 -> "one")
    val sortedMapFold = Iso.id[immutable.SortedMap[Int, String]].asFold
    assertEquals(sortedMapFold.index(0).getAll(sortedMap), Nil)
    assertEquals(sortedMapFold.index(1).getAll(sortedMap), List("one"))
    assertEquals(sortedMap.optics.andThen(sortedMapFold).index(0).getAll, Nil)
    assertEquals(sortedMap.optics.andThen(sortedMapFold).index(1).getAll, List("one"))

    val vector     = Vector(1)
    val vectorFold = Iso.id[Vector[Int]].asFold
    assertEquals(vectorFold.index(0).getAll(vector), List(1))
    assertEquals(vectorFold.index(1).getAll(vector), Nil)
    assertEquals(vector.optics.andThen(vectorFold).index(0).getAll, List(1))
    assertEquals(vector.optics.andThen(vectorFold).index(1).getAll, Nil)

    val chain     = Chain.one(1)
    val chainFold = Iso.id[Chain[Int]].asFold
    assertEquals(chainFold.index(0).getAll(chain), List(1))
    assertEquals(chainFold.index(1).getAll(chain), Nil)
    assertEquals(chain.optics.andThen(chainFold).index(0).getAll, List(1))
    assertEquals(chain.optics.andThen(chainFold).index(1).getAll, Nil)

    val nec     = NonEmptyChain.one(1)
    val necFold = Iso.id[NonEmptyChain[Int]].asFold
    assertEquals(necFold.index(0).getAll(nec), List(1))
    assertEquals(necFold.index(1).getAll(nec), Nil)
    assertEquals(nec.optics.andThen(necFold).index(0).getAll, List(1))
    assertEquals(nec.optics.andThen(necFold).index(1).getAll, Nil)

    val nev     = NonEmptyVector.one(1)
    val nevFold = Iso.id[NonEmptyVector[Int]].asFold
    assertEquals(nevFold.index(0).getAll(nev), List(1))
    assertEquals(nevFold.index(1).getAll(nev), Nil)
    assertEquals(nev.optics.andThen(nevFold).index(0).getAll, List(1))
    assertEquals(nev.optics.andThen(nevFold).index(1).getAll, Nil)

    val nel     = NonEmptyList.one(1)
    val nelFold = Iso.id[NonEmptyList[Int]].asFold
    assertEquals(nelFold.index(0).getAll(nel), List(1))
    assertEquals(nelFold.index(1).getAll(nel), Nil)
    assertEquals(nel.optics.andThen(nelFold).index(0).getAll, List(1))
    assertEquals(nel.optics.andThen(nelFold).index(1).getAll, Nil)
  }
}
