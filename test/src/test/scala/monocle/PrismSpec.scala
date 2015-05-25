package monocle

import monocle.law.discipline.{OptionalTests, PrismTests, SetterTests, TraversalTests}

import scalaz._

class PrismSpec extends MonocleSuite {

  def right[E, A]: Prism[E \/ A, A] = Prism[E \/ A, A](_.toOption)(\/.right)

  checkAll("apply Prism", PrismTests(right[String, Int]))

  checkAll("prism.asTraversal", OptionalTests(right[String, Int].asOptional))
  checkAll("prism.asTraversal", TraversalTests(right[String, Int].asTraversal))
  checkAll("prism.asSetter"   , SetterTests(right[String, Int].asSetter))

  // test implicit resolution of type classes

  test("Prism has a Compose instance") {
    Compose[Prism].compose(right[String, Int], right[String, String \/ Int]).getOption(\/-(\/-(3))) shouldEqual Some(3)
  }

  test("Prism has a Category instance") {
    Category[Prism].id[Int].getOption(3) shouldEqual Some(3)
  }


}