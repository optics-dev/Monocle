package monocle.function

import org.specs2.scalaz.Spec
import monocle.syntax._
import monocle.std._

import scalaz.{==>>, IMap, Maybe}

class EmptyExample extends Spec {

  "empty is a Prism that is successful only when S is empty" in {
    (List(1, 2, 3) applyPrism empty getMaybe) ==== Maybe.empty

    (List.empty[Int]   applyPrism empty getMaybe) ==== Maybe.just(())
    (Vector.empty[Int] applyPrism empty getMaybe) ==== Maybe.just(())
    (""                applyPrism empty getMaybe) ==== Maybe.just(())
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

    _isEmpty(List.empty)   ==== true
    _isEmpty(Option.empty) ==== true
    _isEmpty("")           ==== true
  }
  
}
