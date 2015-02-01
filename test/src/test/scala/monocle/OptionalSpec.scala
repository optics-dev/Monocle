package monocle

import monocle.TestUtil._
import monocle.law.{OptionalLaws, SetterLaws, TraversalLaws}
import org.specs2.scalaz.Spec

import scalaz._
import scalaz.syntax.std.option._

class OptionalSpec extends Spec {

  def headMaybe[A]: Optional[IList[A], A] = Optional[IList[A], A](_.headOption.toMaybe){
    a => {
      case ICons(x, xs) => ICons(a, xs)
      case INil()       => INil()
    }
  }

  checkAll("apply Optional", OptionalLaws(headMaybe[Int]))

  checkAll("optional.asTraversal", TraversalLaws(headMaybe[Int].asTraversal))
  checkAll("optional.asSetter"   , SetterLaws(headMaybe[Int].asSetter))

  // test implicit resolution of type classes

  "Optional has a Compose instance" in {
    Compose[Optional].compose(headMaybe[Int], headMaybe[IList[Int]]).getMaybe(IList(IList(1,2,3), IList(4))) ==== Maybe.just(1)
  }

  "Optional has a Category instance" in {
    Category[Optional].id[Int].getMaybe(3) ==== Maybe.just(3)
  }

  "Optional has a Choice instance" in {
    Choice[Optional].choice(headMaybe[Int], Category[Optional].id[Int]).getMaybe(-\/(IList(1,2,3))) ==== Maybe.just(1)
  }


}
