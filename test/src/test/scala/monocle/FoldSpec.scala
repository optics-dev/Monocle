package monocle

import org.specs2.scalaz.Spec

import scalaz.{Monoid, INil, Maybe, IList}
import scalaz.std.anyVal._
import scalaz.std.string._

class FoldSpec extends Spec {

  val iListFold = Fold[IList, Int]

  "fold" in {
    iListFold.fold(IList(1,2,3,4,5)) ==== 15
  }

  "foldMap" in {
    iListFold.foldMap(_.toString)(IList(1,2,3,4,5)) ==== "12345"
  }

  "headMaybe" in {
    iListFold.headMaybe(IList(1,2,3,4,5)) ==== Maybe.just(1)
    iListFold.headMaybe(INil())           ==== Maybe.empty
  }

  "exist" in {
    iListFold.exist(_ % 2 == 0)(IList(1,2,3)) ==== true
    iListFold.exist(_ == 7)(IList(1,2,3))     ==== false
  }

  "all" in {
    iListFold.all(_ % 2 == 0)(IList(1,2,3)) ==== false
    iListFold.all(_ <= 7)(IList(1,2,3))     ==== true
  }

  "headMaybe is O(1)" in {
    val counterIListFold = CounterFold(iListFold)

    counterIListFold.headMaybe(IList(1,2,3,4,5)) ==== Maybe.just(1)
    counterIListFold.counter ==== 1
  }
}

case class CounterFold[S, A](_fold: Fold[S, A]) extends Fold[S, A] {
  var counter = 0
  def foldMap[M: Monoid](f: A => M)(s: S): M =
    _fold.foldMap{a => counter = counter + 1; f(a)}(s)
}
