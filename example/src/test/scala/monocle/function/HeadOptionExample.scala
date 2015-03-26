package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.IList

class HeadOptionExample extends Spec {

  "headOption creates a Traversal from a List, Stream or Vector to its optional first element" in {
    (List(1,2,3)     applyOptional headOption getOption) ==== Some(1)
    (Stream(1,2,3)   applyOptional headOption getOption) ==== Some(1)
    (Vector(1,2,3)   applyOptional headOption getOption) ==== Some(1)
    (IList(1,2,3)    applyOptional headOption getOption) ==== Some(1)

    (List.empty[Int] applyOptional headOption getOption)     ==== None
    (List.empty[Int] applyOptional headOption modify(_ + 1)) ==== Nil

    (List(1,2,3)     applyOptional headOption set 0)       ==== List(0,2,3)
    (List(1,2,3)     applyOptional headOption setOption 0) ==== Some(List(0,2,3))

    (List.empty[Int] applyOptional headOption set 0)       ==== Nil
    (List.empty[Int] applyOptional headOption setOption 0) ==== None

  }

  "headOption creates a Traversal from a String to its optional head Char" in {
    ("Hello" applyOptional headOption getOption) ==== Some('H')

    ("Hello" applyOptional headOption set 'M') ==== "Mello"
  }

}
