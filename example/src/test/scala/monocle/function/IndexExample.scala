package monocle.function

import monocle.MonocleSuite
import monocle.function.all._
import monocle.std.all._
import monocle.syntax.all._

import scalaz.std.string._
import scalaz.{IMap, OneAnd}

class IndexExample extends MonocleSuite {

  test("index creates an Optional from a Map, IMap to a value at the index") {
    (Map("One" -> 1, "Two" -> 2) applyOptional index("One") getOption) shouldEqual Some(1)
    (IMap("One" -> 1, "Two" -> 2) applyOptional index("One") getOption) shouldEqual Some(1)

    (Map("One" -> 1, "Two" -> 2) applyOptional index("One") set 2) shouldEqual Map("One" -> 2, "Two" -> 2)
    (IMap("One" -> 1, "Two" -> 2) applyOptional index("One") set 2) shouldEqual IMap("One" -> 2, "Two" -> 2)
  }

  test("index creates an Optional from a List, IList, Vector or Stream to a value at the index") {
    (List(0,1,2,3) applyOptional index(1) getOption) shouldEqual Some(1)
    (List(0,1,2,3) applyOptional index(8) getOption) shouldEqual None

    (Vector(0,1,2,3) applyOptional index(1) modify(_ + 1)) shouldEqual Vector(0,2,2,3)
    // setting or modifying a value at an index without value is a no op
    (Stream(0,1,2,3) applyOptional index(64) set 10)       shouldEqual Stream(0,1,2,3)
  }

  test("index creates an Optional from a OneAnd to a value at the index") {
    (OneAnd(1, List(2,3)) applyOptional index(0) getOption) shouldEqual Some(1)
    (OneAnd(1, List(2,3)) applyOptional index(1) getOption) shouldEqual Some(2)
  }

  test("index creates an Optional from a String to a Char") {
    ("Hello World" applyOptional index(2) getOption) shouldEqual Some('l')
  }

  test("index creates an Optional from Int to one of its bit") {
    (3 applyOptional index(0) getOption)  shouldEqual Some(true)   // true  means bit is 1
    (4 applyOptional index(0) getOption)  shouldEqual Some(false)  // false means bit is 0
    (0 applyOptional index(79) getOption) shouldEqual None

    (32 applyOptional index(0) set true)   shouldEqual 33
    (3  applyOptional index(1) modify(!_)) shouldEqual 1 // since we toggled 2nd bit

    // update on an incorrect index is a noop
    (0 applyOptional index(-1) set true)  shouldEqual 0
  }

  test("index creates an Optional from Char to one of its bit") {
    ('x' applyOptional index(0) getOption) shouldEqual Some(false)
    ('x' applyOptional index(0) set true) shouldEqual 'y'
  }

}
