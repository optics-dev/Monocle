package monocle

import monocle.law.discipline.{OptionalTests, SetterTests, TraversalTests}

import scalaz._

class OptionalSpec extends MonocleSuite {

  def headOption[A]: Optional[List[A], A] = Optional[List[A], A](_.headOption){
    a => {
      case x :: xs => a :: xs
      case Nil     => Nil
    }
  }

  checkAll("apply Optional", OptionalTests(headOption[Int]))

  checkAll("optional.asTraversal", TraversalTests(headOption[Int].asTraversal))
  checkAll("optional.asSetter"   , SetterTests(headOption[Int].asSetter))

  test("void"){
    (Iso.id[Int] composeOptional Optional.void[Int, Int]).getOption(1) shouldEqual None
  }

  // test implicit resolution of type classes

  test("Optional has a Compose instance") {
    Compose[Optional].compose(headOption[Int], headOption[List[Int]]).getOption(List(List(1,2,3), List(4))) shouldEqual Some(1)
  }

  test("Optional has a Category instance") {
    Category[Optional].id[Int].getOption(3) shouldEqual Some(3)
  }

  test("Optional has a Choice instance") {
    Choice[Optional].choice(headOption[Int], Category[Optional].id[Int]).getOption(-\/(List(1,2,3))) shouldEqual Some(1)
  }


}
