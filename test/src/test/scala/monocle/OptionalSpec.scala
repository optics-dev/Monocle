package monocle

import monocle.TestUtil._
import monocle.law.{OptionalLaws, SetterLaws, TraversalLaws}
import org.specs2.scalaz.Spec

import scalaz._

class OptionalSpec extends Spec {

  def headOption[A]: Optional[IList[A], A] = Optional[IList[A], A](_.headOption){
    a => {
      case ICons(x, xs) => ICons(a, xs)
      case INil()       => INil()
    }
  }

  checkAll("apply Optional", OptionalLaws(headOption[Int]))

  checkAll("optional.asTraversal", TraversalLaws(headOption[Int].asTraversal))
  checkAll("optional.asSetter"   , SetterLaws(headOption[Int].asSetter))

  // test implicit resolution of type classes

  "Optional has a Compose instance" in {
    Compose[Optional].compose(headOption[Int], headOption[IList[Int]]).getOption(IList(IList(1,2,3), IList(4))) ==== Some(1)
  }

  "Optional has a Category instance" in {
    Category[Optional].id[Int].getOption(3) ==== Some(3)
  }

  "Optional has a Choice instance" in {
    Choice[Optional].choice(headOption[Int], Category[Optional].id[Int]).getOption(-\/(IList(1,2,3))) ==== Some(1)
  }


}
