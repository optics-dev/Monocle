package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.{==>>, IMap}

class EmptyExample extends Spec {

  "empty is a Prism that is successful only when S is empty" in {
    (List(1, 2, 3) applyPrism empty getOption) ==== None

    (List.empty[Int]   applyPrism empty getOption) ==== Some(())
    (Vector.empty[Int] applyPrism empty getOption) ==== Some(())
    (""                applyPrism empty getOption) ==== Some(())
  }

  "_empty return the empty value of a given type" in {
    _empty[List[Int]]        ==== List.empty[Int]
    _empty[Map[Int, String]] ==== Map.empty[Int, String]
    _empty[Int ==>> String]  ==== IMap.empty[Int, String]
    _empty[String]           ==== ""
  }

  "_isEmpty is a function that takes an S and return true is S is empty, false otherwise" in {
    _isEmpty(List(1,2,3)) ==== false
    _isEmpty("hello")     ==== false

    _isEmpty(Nil)  ==== true
    _isEmpty(None) ==== true
    _isEmpty("")   ==== true
  }
  
}
