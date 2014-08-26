package monocle.function

import org.specs2.scalaz.Spec
import monocle.syntax._
import monocle.std._

import scalaz.Maybe

class EmptyExample extends Spec {

  "empty is a Prism that is successful only when S is empty" in {
    (List(1, 2, 3) applyPrism empty getMaybe) shouldEqual Maybe.empty

    (List.empty[Int]   applyPrism empty getMaybe) shouldEqual Maybe.just(())
    (Vector.empty[Int] applyPrism empty getMaybe) shouldEqual Maybe.just(())
    (""                applyPrism empty getMaybe) shouldEqual Maybe.just(())
  }

  "_empty return the empty value of a given type" in {
    _empty[List[Int]]        shouldEqual List.empty[Int]
    _empty[Map[Int, String]] shouldEqual Map.empty[Int, String]
    _empty[String]           shouldEqual ""
  }

  "_isEmpty is a function that takes an S and return true is S is empty, false otherwise" in {
    _isEmpty(List(1,2,3)) shouldEqual false
    _isEmpty("hello")     shouldEqual false

    _isEmpty(Nil)  shouldEqual true
    _isEmpty(None) shouldEqual true
    _isEmpty("")   shouldEqual true
  }
  
}
