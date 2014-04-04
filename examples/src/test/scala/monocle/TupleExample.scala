package monocle

import monocle.std.tuple._
import org.specs2.scalaz.Spec

class TupleExample extends Spec {

  "_1 is a Lens toward the first element of a pair" in {
    val pair: (Int, String) =  (5,"Hello")
    _1.get(pair)    shouldEqual 5
    _1.set(pair, 6) shouldEqual (6, "Hello")

    // can change type as well
    _1.set(pair, "Plop") shouldEqual ("Plop", "Hello")
  }

  "_2 is a Lens toward the second element of a pair" in {
    _2.get((3L, 5.8)) shouldEqual 5.8
  }

  "both is a Traversal from a pair of identical types toward each of its elements" in {
    both.modify((3, 4), (_:Int) + 1) shouldEqual (4, 5)

    import scalaz.std.list._
    both.multiLift((3, 4), {n: Int => List(n-1, n+1)}) shouldEqual List((2,3), (2,5), (4,3), (4,5))
  }



}
