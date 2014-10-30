package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.{IMap, Maybe, OneAnd}
import scalaz.std.string._


class IndexExample extends Spec {

  "index creates 0 or 1 Traversal from a Map, IMap to a value at the index" in {
    (Map("One" -> 1, "Two" -> 2) applyOptional index("One") getMaybe) ==== Maybe.just(1)
    (IMap("One" -> 1, "Two" -> 2) applyOptional index("One") getMaybe) ==== Maybe.just(1)

    (Map("One" -> 1, "Two" -> 2) applyOptional index("One") set 2) ==== Map("One" -> 2, "Two" -> 2)
    (IMap("One" -> 1, "Two" -> 2) applyOptional index("One") set 2) ==== IMap("One" -> 2, "Two" -> 2)
  }

  "index creates 0 or 1 Traversal from a List, IList, Vector or Stream to a value at the index" in {
    (List(0,1,2,3) applyOptional index(1) getMaybe) ==== Maybe.just(1)
    (List(0,1,2,3) applyOptional index(8) getMaybe) ==== Maybe.empty

    (Vector(0,1,2,3) applyOptional index(1) modify(_ + 1)) ==== Vector(0,2,2,3)
    // setting or modifying a value at an index without value is a no op
    (Stream(0,1,2,3) applyOptional index(64) set 10)       ==== Stream(0,1,2,3)
  }

  "index creates 0 or 1 Traversal from a OneAnd to a value at the index" in {
    (OneAnd(1, List(2,3)) applyOptional index(0) getMaybe) ==== Maybe.just(1)
    (OneAnd(1, List(2,3)) applyOptional index(1) getMaybe) ==== Maybe.just(2)
  }

  "index creates 0 or 1 Traversal from a String to a Char" in {

    ("Hello World" applyOptional index(2) getMaybe) ==== Maybe.just('l')

  }

}
