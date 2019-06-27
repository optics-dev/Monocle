package monocle.function

import monocle.MonocleSuite

class LastOptionExample extends MonocleSuite {

  test("lastOption creates a Traversal from a List, Stream or Vector to its optional last element") {
    (List(1,2,3)   applyOptional lastOption getOption) shouldEqual Some(3)
    (Vector(1,2,3) applyOptional lastOption getOption) shouldEqual Some(3)

    (List.empty[Int] applyOptional lastOption getOption)     shouldEqual None
    (List.empty[Int] applyOptional lastOption modify(_ + 1)) shouldEqual Nil

    (List(1,2,3)     applyOptional lastOption set 0) shouldEqual List(1,2,0)
  }

  test("lastOption creates a Traversal from a String to its optional last Char") {
    ("Hello" applyOptional lastOption getOption) shouldEqual Some('o')

    ("Hello" applyOptional lastOption set 'a') shouldEqual "Hella"
  }

}
