package monocle.syntax

import monocle.syntax.all._
import monocle.Setter
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
    tuple._1.set("b")(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_1 = "b"))
    tuple.first.set("b")(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_1 = "b"))
  }

  test("_2") {
    tuple._2.set('c')(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_2 = 'c'))
    tuple.second.set('c')(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_2 = 'c'))
  }

  test("_3") {
    tuple._3.set(false)(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_3 = false))
    tuple.third.set(false)(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_3 = false))
  }

  test("_4") {
    tuple._4.set(2)(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_4 = 2))
    tuple.fourth.set(2)(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_4 = 2))
  }

  test("_5") {
    tuple._5.set(2L)(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_5 = 2L))
    tuple.fifth.set(2L)(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_5 = 2L))
  }

  test("_6") {
    tuple._6.set(2.0)(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_6 = 2.0))
    tuple.sixth.set(2.0)(foo) shouldEqual Foo(mapV, listV, tupleV.copy(_6 = 2.0))
  }

  test("at") {
    map.at(2).set(Some("Four"))(foo) shouldEqual Foo(Map(1 -> "One", 2 -> "Four"), listV, tupleV)
  }
}
