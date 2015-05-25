package monocle

import monocle.law.discipline.{OptionalTests, SetterTests, TraversalTests}

import scalaz._

class OptionalSpec extends MonocleSuite {

  def headOption[A]: Optional[IList[A], A] = Optional[IList[A], A](_.headOption){
    a => {
      case ICons(x, xs) => ICons(a, xs)
      case INil()       => INil()
    }
  }

  checkAll("apply Optional", OptionalTests(headOption[Int]))

  checkAll("optional.asTraversal", TraversalTests(headOption[Int].asTraversal))
  checkAll("optional.asSetter"   , SetterTests(headOption[Int].asSetter))

  // test implicit resolution of type classes

  test("Optional has a Compose instance") {
    Compose[Optional].compose(headOption[Int], headOption[IList[Int]]).getOption(IList(IList(1,2,3), IList(4))) shouldEqual Some(1)
  }

  test("Optional has a Category instance") {
    Category[Optional].id[Int].getOption(3) shouldEqual Some(3)
  }

  test("Optional has a Choice instance") {
    Choice[Optional].choice(headOption[Int], Category[Optional].id[Int]).getOption(-\/(IList(1,2,3))) shouldEqual Some(1)
  }


}
