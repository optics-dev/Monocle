package monocle

import monocle.law.discipline.{OptionalTests, SetterTests, TraversalTests}

import cats.arrow.{Category, Choice, Compose}
import scala.{Left => -\/}

class OptionalSpec extends MonocleSuite {

  def headOption[A]: Optional[List[A], A] = Optional[List[A], A](_.headOption){
    a => {
      case x :: xs => a :: xs
      case Nil     => Nil
    }
  }
  
  def headOptionI: Optional[List[Int], Int] = headOption[Int]
  def headOption2[A, B]: Optional[List[(A, B)], (A, B)] = headOption[(A, B)]

  checkAll("apply Optional", OptionalTests(headOptionI))

  checkAll("optional.asTraversal", TraversalTests(headOptionI.asTraversal))
  checkAll("optional.asSetter"   , SetterTests(headOptionI.asSetter))

  checkAll("first" , OptionalTests(headOptionI.first[Boolean]))
  checkAll("second", OptionalTests(headOptionI.second[Boolean]))

  test("void") {
    Optional.void.getOption("hello") shouldEqual None
    Optional.void.set(5)("hello")    shouldEqual "hello"
  }

  // test implicit resolution of type classes

  test("Optional has a Compose instance") {
    Compose[Optional].compose(headOptionI, headOption[List[Int]]).getOption(List(List(1,2,3), List(4))) shouldEqual Some(1)
  }

  test("Optional has a Category instance") {
    Category[Optional].id[Int].getOption(3) shouldEqual Some(3)
  }

  test("Optional has a Choice instance") {
    Choice[Optional].choice(headOptionI, Category[Optional].id[Int]).getOption(-\/(List(1,2,3))) shouldEqual Some(1)
  }


  test("getOption") {
    headOptionI.getOption(List(1,2,3,4)) shouldEqual Some(1)
    headOptionI.getOption(Nil)           shouldEqual None
  }

  test("isEmpty") {
    headOptionI.isEmpty(List(1,2,3,4)) shouldEqual false
    headOptionI.isEmpty(Nil)           shouldEqual true
  }

  test("nonEmpty") {
    headOptionI.nonEmpty(List(1,2,3,4)) shouldEqual true
    headOptionI.nonEmpty(Nil)           shouldEqual false
  }

  test("find") {
    headOptionI.find(_ > 0)(List(1,2,3,4)) shouldEqual Some(1)
    headOptionI.find(_ > 9)(List(1,2,3,4)) shouldEqual None
  }

  test("exist") {
    headOptionI.exist(_ > 0)(List(1,2,3,4)) shouldEqual true
    headOptionI.exist(_ > 9)(List(1,2,3,4)) shouldEqual false
    headOptionI.exist(_ > 9)(Nil)           shouldEqual false
  }

  test("all") {
    headOptionI.all(_ > 2)(List(1,2,3,4)) shouldEqual false
    headOptionI.all(_ > 0)(List(1,2,3,4)) shouldEqual true
    headOptionI.all(_ > 0)(Nil)           shouldEqual true
  }

  test("set") {
    headOptionI.set(0)(List(1,2,3,4)) shouldEqual List(0,2,3,4)
    headOptionI.set(0)(Nil)           shouldEqual Nil
  }

  test("setOption") {
    headOptionI.setOption(0)(List(1,2,3,4)) shouldEqual Some(List(0,2,3,4))
    headOptionI.setOption(0)(Nil)           shouldEqual None
  }

  test("modify") {
    headOptionI.modify(_ + 1)(List(1,2,3,4)) shouldEqual List(2,2,3,4)
    headOptionI.modify(_ + 1)(Nil)           shouldEqual Nil
  }

  test("modifyOption") {
    headOptionI.modifyOption(_ + 1)(List(1,2,3,4)) shouldEqual Some(List(2,2,3,4))
    headOptionI.modifyOption(_ + 1)(Nil)           shouldEqual None
  }

}
