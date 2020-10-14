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
    Compose[Fold]
      .compose(eachLi, nestedListFold[Int])
      .fold(List(List(1, 2, 3), List(4, 5), List(6))) shouldEqual 21
  }

  test("Fold has a Category instance") {
    Category[Fold].id[Int].fold(3) shouldEqual 3
  }

  test("Fold has a Choice instance") {
    Choice[Fold]
      .choice(eachLi, Choice[Fold].id[Int])
      .fold(Left(List(1, 2, 3))) shouldEqual 6
  }

  test("foldMap") {
    eachLi.foldMap(_.toString)(List(1, 2, 3, 4, 5)) shouldEqual "12345"
  }

  test("getAll") {
    eachLi.getAll(List(1, 2, 3, 4)) shouldEqual List(1, 2, 3, 4)
  }

  test("headOption") {
    eachLi.headOption(List(1, 2, 3, 4)) shouldEqual Some(1)
  }

  test("lastOption") {
    eachLi.lastOption(List(1, 2, 3, 4)) shouldEqual Some(4)
  }

  test("length") {
    eachLi.length(List(1, 2, 3, 4)) shouldEqual 4
    eachLi.length(Nil) shouldEqual 0
  }

  test("isEmpty") {
    eachLi.isEmpty(List(1, 2, 3, 4)) shouldEqual false
    eachLi.isEmpty(Nil) shouldEqual true
  }

  test("nonEmpty") {
    eachLi.nonEmpty(List(1, 2, 3, 4)) shouldEqual true
    eachLi.nonEmpty(Nil) shouldEqual false
  }

  test("find") {
    eachLi.find(_ > 2)(List(1, 2, 3, 4)) shouldEqual Some(3)
    eachLi.find(_ > 9)(List(1, 2, 3, 4)) shouldEqual None
  }

  test("exist") {
    eachLi.exist(_ > 2)(List(1, 2, 3, 4)) shouldEqual true
    eachLi.exist(_ > 9)(List(1, 2, 3, 4)) shouldEqual false
    eachLi.exist(_ > 9)(Nil) shouldEqual false
  }

  test("all") {
    eachLi.all(_ > 2)(List(1, 2, 3, 4)) shouldEqual false
    eachLi.all(_ > 0)(List(1, 2, 3, 4)) shouldEqual true
    eachLi.all(_ > 0)(Nil) shouldEqual true
  }

  test("select (satisfied predicate)") {
    val select = Fold.select[List[Int]](_.endsWith(List(2, 3)))
    select.getAll(List(1, 2, 3)) shouldEqual List(List(1, 2, 3))
  }

  test("select (unsatisfied predicate)") {
    val select = Fold.select[List[Int]](_.endsWith(List(2, 3)))
    select.getAll(List(1, 2, 3, 4)) shouldEqual List()
  }

  test("to") {
    eachLi.to(_.toString()).getAll(List(1, 2, 3)) shouldEqual List("1", "2", "3")
  }

  test("some") {
    val numbers = List(Some(1), None, Some(2), None)
    val fold = Fold.fromFoldable[List, Option[Int]]

    fold.some.getAll(numbers) shouldEqual List(1,2)
    numbers.applyFold(fold).some.getAll shouldEqual List(1,2)
  }

  test("withDefault") {
    val numbers = List(Some(1), None, Some(2), None)
    val fold = Fold.fromFoldable[List, Option[Int]]

    fold.withDefault(0).getAll(numbers) shouldEqual List(1,0,2,0)
    numbers.applyFold(fold).withDefault(0).getAll shouldEqual List(1,0,2,0)
  }

  test("each") {
    val numbers = List(List(1,2,3), Nil, List(4), Nil)
    val fold = Fold.fromFoldable[List, List[Int]]

    fold.each.getAll(numbers) shouldEqual List(1, 2, 3,4)
    numbers.applyFold(fold).each.getAll shouldEqual List(1, 2, 3, 4)
  }
}
