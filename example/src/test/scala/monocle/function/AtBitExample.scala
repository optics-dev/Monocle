package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec


class AtBitExample extends Spec {

  "atBit creates a Lens from Int to one of its bit" in {
    (3 applyLens atBit(0) get)  shouldEqual true  // true  means bit is 1
    (4 applyLens atBit(0) get) shouldEqual false  // false means bit is 0

    (32 applyLens atBit(0) set true)   shouldEqual 33
    (3  applyLens atBit(1) modify(!_)) shouldEqual 1 // since we toggled 2nd bit

    // negative index starts from the most significant bit
    (0 applyLens atBit(-1) set true)  shouldEqual -2147483648
  }


  "atBit creates a Lens from Char to one of its bit" in {
    ('x' applyLens atBit(0) get)      shouldEqual false
    ('x' applyLens atBit(0) set true) shouldEqual 'y'
  }

  // same for all other classes having a AtBit instances


}
