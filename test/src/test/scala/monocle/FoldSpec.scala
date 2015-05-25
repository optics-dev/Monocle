package monocle

import scalaz._
import scalaz.std.anyVal._
import scalaz.std.string._

class FoldSpec extends MonocleSuite {

  val iListFold = Fold.fromFoldable[IList, Int]

  test("foldMap") {
    iListFold.foldMap(_.toString)(IList(1,2,3,4,5)) shouldEqual "12345"
  }

  test("headMaybe") {
    iListFold.headOption(IList(1,2,3,4,5)) shouldEqual Some(1)
    iListFold.headOption(INil())           shouldEqual None
  }

  test("exist") {
    iListFold.exist(_ % 2 == 0)(IList(1,2,3)) shouldEqual true
    iListFold.exist(_ == 7)(IList(1,2,3))     shouldEqual false
  }

  test("all") {
    iListFold.all(_ % 2 == 0)(IList(1,2,3)) shouldEqual false
    iListFold.all(_ <= 7)(IList(1,2,3))     shouldEqual true
  }

  def nestedIListFold[A] = new Fold[IList[IList[A]], IList[A]]{
    def foldMap[M: Monoid](f: (IList[A]) => M)(s: IList[IList[A]]): M =
      s.foldRight(Monoid[M].zero)((l, acc) => Monoid[M].append(f(l), acc))
  }

  // test implicit resolution of type classes

  test("Fold has a Compose instance") {
    Compose[Fold].compose(iListFold, nestedIListFold[Int]).fold(IList(IList(1,2,3), IList(4,5), IList(6))) shouldEqual 21
  }

  test("Fold has a Category instance") {
    Category[Fold].id[Int].fold(3) shouldEqual 3
  }

  test("Fold has a Choice instance") {
    Choice[Fold].choice(iListFold, Choice[Fold].id[Int]).fold(-\/(IList(1,2,3))) shouldEqual 6
  }

}