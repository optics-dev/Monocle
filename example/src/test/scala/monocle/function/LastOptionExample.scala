package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.IList

class LastOptionExample extends Spec {

  "lastOption creates a Traversal from a List, IList, Stream or Vector to its optional last element" in {
    (List(1,2,3)   applyOptional lastOption getOption) ==== Some(3)
    (IList(1,2,3)  applyOptional lastOption getOption) ==== Some(3)
    (Stream(1,2,3) applyOptional lastOption getOption) ==== Some(3)
    (Vector(1,2,3) applyOptional lastOption getOption) ==== Some(3)

    (List.empty[Int] applyOptional lastOption getOption)     ==== None
    (List.empty[Int] applyOptional lastOption modify(_ + 1)) ==== Nil

    (List(1,2,3)     applyOptional lastOption set 0) ==== List(1,2,0)
  }

  "lastOption creates a Traversal from a String to its optional last Char" in {
    ("Hello" applyOptional lastOption getOption) ==== Some('o')

    ("Hello" applyOptional lastOption set 'a') ==== "Hella"
  }

}
