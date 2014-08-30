package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.{Maybe, IList}


class HeadMaybeExample extends Spec {

  "headMaybe creates a Traversal from a List, Stream or Vector to its optional first element" in {
    (List(1,2,3)     applyOptional headMaybe getMaybe) ==== Maybe.just(1)
    (Stream(1,2,3)   applyOptional headMaybe getMaybe) ==== Maybe.just(1)
    (Vector(1,2,3)   applyOptional headMaybe getMaybe) ==== Maybe.just(1)
    (IList(1,2,3)    applyOptional headMaybe getMaybe) ==== Maybe.just(1)

    (List.empty[Int] applyOptional headMaybe getMaybe)     ==== Maybe.empty
    (List.empty[Int] applyOptional headMaybe modify(_ + 1)) ==== Nil

    (List(1,2,3)     applyOptional headMaybe set 0)      ==== List(0,2,3)
    (List(1,2,3)     applyOptional headMaybe setMaybe 0) ==== Maybe.just(List(0,2,3))

    (List.empty[Int] applyOptional headMaybe set 0)      ==== Nil
    (List.empty[Int] applyOptional headMaybe setMaybe 0) ==== Maybe.empty

  }

  "headMaybe creates a Traversal from a String to its optional head Char" in {
    ("Hello" applyOptional headMaybe getMaybe) ==== Maybe.just('H')

    ("Hello" applyOptional headMaybe set 'M') ==== "Mello"
  }

}
