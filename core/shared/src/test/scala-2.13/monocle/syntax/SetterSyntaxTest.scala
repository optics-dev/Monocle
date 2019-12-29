package monocle.syntax

import monocle.Setter
import monocle.implicits._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SetterSyntaxTest extends AnyFunSuite with Matchers {
  case class Foo(map: Map[Int, String], list: List[Int], tuple: (String, Char, Boolean, Int, Long, Double))
  val mapV   = Map(1 -> "One", 2 -> "Two")
  val tupleV = ("a", 'b', true, 1, 1L, 1.0)
  val listV  = List(1, 2, 3)
  val foo = Foo(
    map = mapV,
    list = listV,
    tuple = tupleV
  )
  val tuple: Setter[Foo, (String, Char, Boolean, Int, Long, Double)] = Setter(f => p => p.copy(tuple = f(p.tuple)))
  val map: Setter[Foo, Map[Int, String]]                             = Setter(f => p => p.copy(map = f(p.map)))

  test("_1") {
    foo.optic(tuple)._1.set("b") shouldEqual Foo(mapV, listV, tupleV.copy(_1 = "b"))
    foo.optic(tuple).first.set("b") shouldEqual Foo(mapV, listV, tupleV.copy(_1 = "b"))
  }

  test("_2") {
    foo.optic(tuple)._2.set('c') shouldEqual Foo(mapV, listV, tupleV.copy(_2 = 'c'))
    foo.optic(tuple).second.set('c') shouldEqual Foo(mapV, listV, tupleV.copy(_2 = 'c'))
  }

  test("_3") {
    foo.optic(tuple)._3.set(false) shouldEqual Foo(mapV, listV, tupleV.copy(_3 = false))
    foo.optic(tuple).third.set(false) shouldEqual Foo(mapV, listV, tupleV.copy(_3 = false))
  }

  test("_4") {
    foo.optic(tuple)._4.set(2) shouldEqual Foo(mapV, listV, tupleV.copy(_4 = 2))
    foo.optic(tuple).fourth.set(2) shouldEqual Foo(mapV, listV, tupleV.copy(_4 = 2))
  }

  test("_5") {
    foo.optic(tuple)._5.set(2L) shouldEqual Foo(mapV, listV, tupleV.copy(_5 = 2L))
    foo.optic(tuple).fifth.set(2L) shouldEqual Foo(mapV, listV, tupleV.copy(_5 = 2L))
  }

  test("_6") {
    foo.optic(tuple)._6.set(2.0) shouldEqual Foo(mapV, listV, tupleV.copy(_6 = 2.0))
    foo.optic(tuple).sixth.set(2.0) shouldEqual Foo(mapV, listV, tupleV.copy(_6 = 2.0))
  }

  test("at") {
    foo.optic(map).at(2).set(Some("Four")) shouldEqual Foo(Map(1 -> "One", 2 -> "Four"), listV, tupleV)
  }
}
