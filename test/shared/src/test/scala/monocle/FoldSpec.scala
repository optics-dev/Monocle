package monocle

import cats.Monoid
import cats.arrow.{Category, Choice, Compose}

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
}
