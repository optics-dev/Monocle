package monocle

import monocle.function.Reverse._
import monocle.syntax.iso._
import org.specs2.scalaz.Spec

class ReverseExample extends Spec {

  "reverse creates an Iso between a List and its reversed version" in {

    (List(1,2,3) <-> reverse get) shouldEqual List(3,2,1)

  }

  "reverse creates an Iso between a Stream and its reversed version" in {
    // Todo: look at infinite case
    (Stream(1,2,3) <-> reverse get) shouldEqual Stream(3,2,1)

  }

  "reverse creates an Iso between a String and its reversed version" in {

    ("Hello" <-> reverse get) shouldEqual "olleH"

  }

}
