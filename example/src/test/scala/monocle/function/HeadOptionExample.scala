package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.{Maybe, IList}


class HeadOptionExample extends Spec {

  "headOption creates a Traversal from a List, Stream or Vector to its optional first element" in {
    (List(1,2,3)     applyOptional headOption getMaybe) shouldEqual Maybe.just(1)
    (Stream(1,2,3)   applyOptional headOption getMaybe) shouldEqual Maybe.just(1)
    (Vector(1,2,3)   applyOptional headOption getMaybe) shouldEqual Maybe.just(1)
    (IList(1,2,3)    applyOptional headOption getMaybe) shouldEqual Maybe.just(1)

    (List.empty[Int] applyOptional headOption getMaybe)     shouldEqual Maybe.empty
    (List.empty[Int] applyOptional headOption modify(_ + 1)) shouldEqual Nil

    (List(1,2,3)     applyOptional headOption set 0)      shouldEqual List(0,2,3)
    (List(1,2,3)     applyOptional headOption setMaybe 0) shouldEqual Maybe.just(List(0,2,3))

    (List.empty[Int] applyOptional headOption set 0)      shouldEqual Nil
    (List.empty[Int] applyOptional headOption setMaybe 0) shouldEqual Maybe.empty

  }

  "headOption creates a Traversal from a String to its optional head Char" in {
    ("Hello" applyOptional headOption getMaybe) shouldEqual Maybe.just('H')

    ("Hello" applyOptional headOption set 'M') shouldEqual "Mello"
  }

  "headOption creates a Traversal from an Option to its optional element" in {
    (Some(1)             applyOptional headOption getMaybe) shouldEqual Maybe.just(1)
    ((None: Option[Int]) applyOptional headOption getMaybe) shouldEqual Maybe.empty
  }

}
