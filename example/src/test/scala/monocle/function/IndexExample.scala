package monocle.function

import monocle.MonocleSuite

import scala.collection.immutable.Map
import cats.data.OneAnd

class IndexExample extends MonocleSuite {

  test("index creates an Optional from a Map, IMap to a value at the index") {
    (Map("One" -> 1, "Two" -> 2) applyOptional index("One") getOption) shouldEqual Some(1)

    (Map("One" -> 1, "Two" -> 2) applyOptional index("One") set 2) shouldEqual Map("One" -> 2, "Two" -> 2)
  }

// TODO commented for 2.13
//  test("index creates an Optional from a List, IList, Vector or Stream to a value at the index") {
//    (List(0,1,2,3) applyOptional index(1) getOption) shouldEqual Some(1)
//    (List(0,1,2,3) applyOptional index(8) getOption) shouldEqual None
//
//    (Vector(0,1,2,3) applyOptional index(1) modify(_ + 1)) shouldEqual Vector(0,2,2,3)
//    // setting or modifying a value at an index without value is a no op
//    (Stream(0,1,2,3) applyOptional index(64) set 10)       shouldEqual Stream(0,1,2,3)
//  }

  test("index creates an Optional from a OneAnd to a value at the index") {
    (OneAnd(1, List(2,3)) applyOptional index(0) getOption) shouldEqual Some(1)
    (OneAnd(1, List(2,3)) applyOptional index(1) getOption) shouldEqual Some(2)
  }

  test("index creates an Optional from a String to a Char") {
    ("Hello World" applyOptional index(2) getOption) shouldEqual Some('l')
  }

}
