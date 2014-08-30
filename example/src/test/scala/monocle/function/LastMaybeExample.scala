package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.{Maybe, IList}


class LastMaybeExample extends Spec {

  "lastMaybe creates a Traversal from a List, IList, Stream or Vector to its optional last element" in {
    (List(1,2,3)   applyOptional lastMaybe getMaybe) ==== Maybe.just(3)
    (IList(1,2,3)  applyOptional lastMaybe getMaybe) ==== Maybe.just(3)
    (Stream(1,2,3) applyOptional lastMaybe getMaybe) ==== Maybe.just(3)
    (Vector(1,2,3) applyOptional lastMaybe getMaybe) ==== Maybe.just(3)

    (List.empty[Int] applyOptional lastMaybe getMaybe)    ==== Maybe.empty
    (List.empty[Int] applyOptional lastMaybe modify(_ + 1)) ==== Nil

    (List(1,2,3)     applyOptional lastMaybe set 0) ==== List(1,2,0)
  }

  "lastMaybe creates a Traversal from a String to its optional last Char" in {
    ("Hello" applyOptional lastMaybe getMaybe) ==== Maybe.just('o')

    ("Hello" applyOptional lastMaybe set 'a') ==== "Hella"
  }

}
