package monocle

import monocle.syntax.lens._
import monocle.function.AtBit._
import org.specs2.scalaz.Spec


class AtBitExample extends Spec {

  "atBit creates a Lens from Int to one of its bit" in {
    (3 |-> atBit(0) get)  shouldEqual true  // true  means bit is 1
    (4 |-> atBit(0) get) shouldEqual false  // false means bit is 0

    (32 |-> atBit(0) set true)   shouldEqual 33
    (3  |-> atBit(1) modify(!_)) shouldEqual 1 // since we toggled 2nd bit

    // negative index starts from the most significant bit
    (0 |-> atBit(-1) set true)  shouldEqual -2147483648
  }


  "atBit creates a Lens from Char to one of its bit" in {
    ('x' |-> atBit(0) get)      shouldEqual false
    ('x' |-> atBit(0) set true) shouldEqual 'y'
  }

  // same for all other classes having a AtBit instances


}
