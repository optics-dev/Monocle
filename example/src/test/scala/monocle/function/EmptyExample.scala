package monocle.function

import org.specs2.scalaz.Spec
import monocle.syntax._
import monocle.std._

class EmptyExample extends Spec {

  "empty is a Prism that is successful only when S is empty" in {
    (List(1, 2, 3)   <-? empty getOption) shouldEqual None

    (List.empty[Int]   <-? empty getOption) shouldEqual Some(())
    (Vector.empty[Int] <-? empty getOption) shouldEqual Some(())
    (""                <-? empty getOption) shouldEqual Some(())
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
