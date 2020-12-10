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
    assertEquals(numbers.applyFold(fold).some.getAll, List(1, 2))
  }

  test("withDefault") {
    val numbers = List(Some(1), None, Some(2), None)
    val fold    = Fold.fromFoldable[List, Option[Int]]

    assertEquals(fold.withDefault(0).getAll(numbers), List(1, 0, 2, 0))
    assertEquals(numbers.applyFold(fold).withDefault(0).getAll, List(1, 0, 2, 0))
  }

  test("each") {
    val numbers = List(List(1, 2, 3), Nil, List(4), Nil)
    val fold    = Fold.fromFoldable[List, List[Int]]

    assertEquals(fold.each.getAll(numbers), List(1, 2, 3, 4))
    assertEquals(numbers.applyFold(fold).each.getAll, List(1, 2, 3, 4))
  }

  test("filter") {
    val numbers = List(1, 2, 3)
    val fold    = Fold.fromFoldable[List, Int]

    assertEquals(fold.filter(_ > 1).getAll(numbers), List(2, 3))
    assertEquals(numbers.applyFold(fold).filter(_ > 1).getAll, List(2, 3))
  }

  test("at") {
    val tuple2     = (1, 2)
    val tuple2Fold = Fold.id[(Int, Int)]
    assertEquals(tuple2Fold.at(1).getAll(tuple2), List(1))
    assertEquals(tuple2Fold.at(2).getAll(tuple2), List(2))
    assertEquals(tuple2.applyFold(tuple2Fold).at(1).getAll, List(1))
    assertEquals(tuple2.applyFold(tuple2Fold).at(2).getAll, List(2))

    val tuple3     = (1, 2, 3)
    val tuple3Fold = Fold.id[(Int, Int, Int)]
    assertEquals(tuple3Fold.at(1).getAll(tuple3), List(1))
    assertEquals(tuple3Fold.at(2).getAll(tuple3), List(2))
    assertEquals(tuple3Fold.at(3).getAll(tuple3), List(3))
    assertEquals(tuple3.applyFold(tuple3Fold).at(1).getAll, List(1))
    assertEquals(tuple3.applyFold(tuple3Fold).at(2).getAll, List(2))
    assertEquals(tuple3.applyFold(tuple3Fold).at(3).getAll, List(3))

    val tuple4     = (1, 2, 3, 4)
    val tuple4Fold = Fold.id[(Int, Int, Int, Int)]
    assertEquals(tuple4Fold.at(1).getAll(tuple4), List(1))
    assertEquals(tuple4Fold.at(2).getAll(tuple4), List(2))
    assertEquals(tuple4Fold.at(3).getAll(tuple4), List(3))
    assertEquals(tuple4Fold.at(4).getAll(tuple4), List(4))
    assertEquals(tuple4.applyFold(tuple4Fold).at(1).getAll, List(1))
    assertEquals(tuple4.applyFold(tuple4Fold).at(2).getAll, List(2))
    assertEquals(tuple4.applyFold(tuple4Fold).at(3).getAll, List(3))
    assertEquals(tuple4.applyFold(tuple4Fold).at(4).getAll, List(4))

    val tuple5     = (1, 2, 3, 4, 5)
    val tuple5Fold = Fold.id[(Int, Int, Int, Int, Int)]
    assertEquals(tuple5Fold.at(1).getAll(tuple5), List(1))
    assertEquals(tuple5Fold.at(2).getAll(tuple5), List(2))
    assertEquals(tuple5Fold.at(3).getAll(tuple5), List(3))
    assertEquals(tuple5Fold.at(4).getAll(tuple5), List(4))
    assertEquals(tuple5Fold.at(5).getAll(tuple5), List(5))
    assertEquals(tuple5.applyFold(tuple5Fold).at(1).getAll, List(1))
    assertEquals(tuple5.applyFold(tuple5Fold).at(2).getAll, List(2))
    assertEquals(tuple5.applyFold(tuple5Fold).at(3).getAll, List(3))
    assertEquals(tuple5.applyFold(tuple5Fold).at(4).getAll, List(4))
    assertEquals(tuple5.applyFold(tuple5Fold).at(5).getAll, List(5))

    val tuple6     = (1, 2, 3, 4, 5, 6)
    val tuple6Fold = Fold.id[(Int, Int, Int, Int, Int, Int)]
    assertEquals(tuple6Fold.at(1).getAll(tuple6), List(1))
    assertEquals(tuple6Fold.at(2).getAll(tuple6), List(2))
    assertEquals(tuple6Fold.at(3).getAll(tuple6), List(3))
    assertEquals(tuple6Fold.at(4).getAll(tuple6), List(4))
    assertEquals(tuple6Fold.at(5).getAll(tuple6), List(5))
    assertEquals(tuple6Fold.at(6).getAll(tuple6), List(6))
    assertEquals(tuple6.applyFold(tuple6Fold).at(1).getAll, List(1))
    assertEquals(tuple6.applyFold(tuple6Fold).at(2).getAll, List(2))
    assertEquals(tuple6.applyFold(tuple6Fold).at(3).getAll, List(3))
    assertEquals(tuple6.applyFold(tuple6Fold).at(4).getAll, List(4))
    assertEquals(tuple6.applyFold(tuple6Fold).at(5).getAll, List(5))
    assertEquals(tuple6.applyFold(tuple6Fold).at(6).getAll, List(6))

    val sortedMap     = immutable.SortedMap(1 -> "one")
    val sortedMapFold = Fold.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapFold.at(1).getAll(sortedMap), List(Some("one")))
    assertEquals(sortedMapFold.at(0).getAll(sortedMap), List(None))
    assertEquals(sortedMap.applyFold(sortedMapFold).at(1).getAll, List(Some("one")))
    assertEquals(sortedMap.applyFold(sortedMapFold).at(0).getAll, List(None))

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapFold = Fold.id[immutable.ListMap[Int, String]]
    assertEquals(listMapFold.at(1).getAll(listMap), List(Some("one")))
    assertEquals(listMapFold.at(0).getAll(listMap), List(None))
    assertEquals(listMap.applyFold(listMapFold).at(1).getAll, List(Some("one")))
    assertEquals(listMap.applyFold(listMapFold).at(0).getAll, List(None))

    val map     = immutable.Map(1 -> "one")
    val mapFold = Fold.id[Map[Int, String]]
    assertEquals(mapFold.at(1).getAll(map), List(Some("one")))
    assertEquals(mapFold.at(0).getAll(map), List(None))
    assertEquals(map.applyFold(mapFold).at(1).getAll, List(Some("one")))
    assertEquals(map.applyFold(mapFold).at(0).getAll, List(None))

    val set     = Set(1)
    val setFold = Fold.id[Set[Int]]
    assertEquals(setFold.at(1).getAll(set), List(true))
    assertEquals(setFold.at(0).getAll(set), List(false))
    assertEquals(set.applyFold(setFold).at(1).getAll, List(true))
    assertEquals(set.applyFold(setFold).at(0).getAll, List(false))
  }

  test("index") {
    val list     = List(1)
    val listFold = Fold.id[List[Int]]
    assertEquals(listFold.index(0).getAll(list), List(1))
    assertEquals(listFold.index(1).getAll(list), Nil)
    assertEquals(list.applyFold(listFold).index(0).getAll, List(1))
    assertEquals(list.applyFold(listFold).index(1).getAll, Nil)

    val lazyList     = LazyList(1)
    val lazyListFold = Fold.id[LazyList[Int]]
    assertEquals(lazyListFold.index(0).getAll(lazyList), List(1))
    assertEquals(lazyListFold.index(1).getAll(lazyList), Nil)
    assertEquals(lazyList.applyFold(lazyListFold).index(0).getAll, List(1))
    assertEquals(lazyList.applyFold(lazyListFold).index(1).getAll, Nil)

    val listMap     = immutable.ListMap(1 -> "one")
    val listMapFold = Fold.id[immutable.ListMap[Int, String]]
    assertEquals(listMapFold.index(0).getAll(listMap), Nil)
    assertEquals(listMapFold.index(1).getAll(listMap), List("one"))
    assertEquals(listMap.applyFold(listMapFold).index(0).getAll, Nil)
    assertEquals(listMap.applyFold(listMapFold).index(1).getAll, List("one"))

    val map     = Map(1 -> "one")
    val mapFold = Fold.id[Map[Int, String]]
    assertEquals(mapFold.index(0).getAll(map), Nil)
    assertEquals(mapFold.index(1).getAll(map), List("one"))
    assertEquals(map.applyFold(mapFold).index(0).getAll, Nil)
    assertEquals(map.applyFold(mapFold).index(1).getAll, List("one"))

    val sortedMap     = immutable.SortedMap(1 -> "one")
    val sortedMapFold = Fold.id[immutable.SortedMap[Int, String]]
    assertEquals(sortedMapFold.index(0).getAll(sortedMap), Nil)
    assertEquals(sortedMapFold.index(1).getAll(sortedMap), List("one"))
    assertEquals(sortedMap.applyFold(sortedMapFold).index(0).getAll, Nil)
    assertEquals(sortedMap.applyFold(sortedMapFold).index(1).getAll, List("one"))

    val vector     = Vector(1)
    val vectorFold = Fold.id[Vector[Int]]
    assertEquals(vectorFold.index(0).getAll(vector), List(1))
    assertEquals(vectorFold.index(1).getAll(vector), Nil)
    assertEquals(vector.applyFold(vectorFold).index(0).getAll, List(1))
    assertEquals(vector.applyFold(vectorFold).index(1).getAll, Nil)

    val chain     = Chain.one(1)
    val chainFold = Fold.id[Chain[Int]]
    assertEquals(chainFold.index(0).getAll(chain), List(1))
    assertEquals(chainFold.index(1).getAll(chain), Nil)
    assertEquals(chain.applyFold(chainFold).index(0).getAll, List(1))
    assertEquals(chain.applyFold(chainFold).index(1).getAll, Nil)

    val nec     = NonEmptyChain.one(1)
    val necFold = Fold.id[NonEmptyChain[Int]]
    assertEquals(necFold.index(0).getAll(nec), List(1))
    assertEquals(necFold.index(1).getAll(nec), Nil)
    assertEquals(nec.applyFold(necFold).index(0).getAll, List(1))
    assertEquals(nec.applyFold(necFold).index(1).getAll, Nil)

    val nev     = NonEmptyVector.one(1)
    val nevFold = Fold.id[NonEmptyVector[Int]]
    assertEquals(nevFold.index(0).getAll(nev), List(1))
    assertEquals(nevFold.index(1).getAll(nev), Nil)
    assertEquals(nev.applyFold(nevFold).index(0).getAll, List(1))
    assertEquals(nev.applyFold(nevFold).index(1).getAll, Nil)

    val nel     = NonEmptyList.one(1)
    val nelFold = Fold.id[NonEmptyList[Int]]
    assertEquals(nelFold.index(0).getAll(nel), List(1))
    assertEquals(nelFold.index(1).getAll(nel), Nil)
    assertEquals(nel.applyFold(nelFold).index(0).getAll, List(1))
    assertEquals(nel.applyFold(nelFold).index(1).getAll, Nil)
  }
}
