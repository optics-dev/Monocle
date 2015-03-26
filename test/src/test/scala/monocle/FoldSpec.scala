package monocle

import org.specs2.scalaz.Spec

import scalaz._
import scalaz.std.anyVal._
import scalaz.std.string._

class FoldSpec extends Spec {

  val iListFold = Fold.fromFoldable[IList, Int]

  "foldMap" in {
    iListFold.foldMap(_.toString)(IList(1,2,3,4,5)) ==== "12345"
  }

  "headMaybe" in {
    iListFold.headOption(IList(1,2,3,4,5)) ==== Some(1)
    iListFold.headOption(INil())           ==== None
  }

  "exist" in {
    iListFold.exist(_ % 2 == 0)(IList(1,2,3)) ==== true
    iListFold.exist(_ == 7)(IList(1,2,3))     ==== false
  }

  "all" in {
    iListFold.all(_ % 2 == 0)(IList(1,2,3)) ==== false
    iListFold.all(_ <= 7)(IList(1,2,3))     ==== true
  }

  def nestedIListFold[A] = new Fold[IList[IList[A]], IList[A]]{
    def foldMap[M: Monoid](f: (IList[A]) => M)(s: IList[IList[A]]): M =
      s.foldRight(Monoid[M].zero)((l, acc) => Monoid[M].append(f(l), acc))
  }

  // test implicit resolution of type classes

  "Fold has a Compose instance" in {
    Compose[Fold].compose(iListFold, nestedIListFold[Int]).fold(IList(IList(1,2,3), IList(4,5), IList(6))) ==== 21
  }

  "Fold has a Category instance" in {
    Category[Fold].id[Int].fold(3) ==== 3
  }

  "Fold has a Choice instance" in {
    Choice[Fold].choice(iListFold, Choice[Fold].id[Int]).fold(-\/(IList(1,2,3))) ==== 6
  }

}