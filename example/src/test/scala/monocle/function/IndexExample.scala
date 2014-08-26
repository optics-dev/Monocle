package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.{Maybe, OneAnd}


class IndexExample extends Spec {

  "index creates 0 or 1 Traversal from a Map to a value at the index" in {
    (Map("One" -> 1, "Two" -> 2) applyOptional index("One") getMaybe) shouldEqual Maybe.just(1)

    (Map("One" -> 1, "Two" -> 2) applyOptional index("One") set 2) shouldEqual Map("One" -> 2, "Two" -> 2)
  }

  "index creates 0 or 1 Traversal from a List, IList, Vector or Stream to a value at the index" in {
    (List(0,1,2,3) applyOptional index(1) getMaybe) shouldEqual Maybe.just(1)
    (List(0,1,2,3) applyOptional index(8) getMaybe) shouldEqual Maybe.empty

    (Vector(0,1,2,3) applyOptional index(1) modify(_ + 1)) shouldEqual Vector(0,2,2,3)
    // setting or modifying a value at an index without value is a no op
    (Stream(0,1,2,3) applyOptional index(64) set 10)       shouldEqual Stream(0,1,2,3)
  }

  "index creates 0 or 1 Traversal from a OneAnd to a value at the index" in {
    (OneAnd(1, List(2,3)) applyOptional index(0) getMaybe) shouldEqual Maybe.just(1)
    (OneAnd(1, List(2,3)) applyOptional index(1) getMaybe) shouldEqual Maybe.just(2)
  }

  "index creates 0 or 1 Traversal from a String to a Char" in {

    ("Hello World" applyOptional index(2) getMaybe) shouldEqual Maybe.just('l')

  }

}
