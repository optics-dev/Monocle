package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.{Maybe, IList}


class LastOptionExample extends Spec {

  "lastOption creates a Traversal from a List, IList, Stream or Vector to its optional last element" in {
    (List(1,2,3)   applyOptional lastOption getMaybe) ==== Maybe.just(3)
    (IList(1,2,3)  applyOptional lastOption getMaybe) ==== Maybe.just(3)
    (Stream(1,2,3) applyOptional lastOption getMaybe) ==== Maybe.just(3)
    (Vector(1,2,3) applyOptional lastOption getMaybe) ==== Maybe.just(3)

    (List.empty[Int] applyOptional lastOption getMaybe)    ==== Maybe.empty
    (List.empty[Int] applyOptional lastOption modify(_ + 1)) ==== Nil

    (List(1,2,3)     applyOptional lastOption set 0) ==== List(1,2,0)
  }

  "lastOption creates a Traversal from a String to its optional last Char" in {
    ("Hello" applyOptional lastOption getMaybe) ==== Maybe.just('o')

    ("Hello" applyOptional lastOption set 'a') ==== "Hella"
  }

  "lastOption creates a Traversal from an Option to its optional element" in {
    (Some(1)             applyOptional lastOption getMaybe) ==== Maybe.just(1)
    ((None: Option[Int]) applyOptional lastOption getMaybe) ==== Maybe.empty
  }

}
