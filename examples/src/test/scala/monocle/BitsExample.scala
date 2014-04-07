package monocle

import monocle.syntax.lens._
import monocle.util.Bits._
import org.specs2.scalaz.Spec


class BitsExample extends Spec {

  "atBit creates a Lens from Int to one of its bit" in {
    import monocle.std.int._
    val intFirstBit: SimpleLens[Int, Boolean] = atBit[Int](0)

    intFirstBit.get(3)  shouldEqual true   // true means bit is 1
    intFirstBit.get(32) shouldEqual false  // false means bit is 0

    intFirstBit.set(32, true) shouldEqual 33

    // Similarly with some syntax sugar

    (3 |-> atBit(1) get)        shouldEqual true
    (3 |-> atBit(1) modify(!_)) shouldEqual 1 // since we toggled 2nd bit

    // negative index starts from most significant bit
    (0 |-> atBit(-1) set true)  shouldEqual -2147483648
  }


  "atBit creates a Lens from Char to one of its bit" in {
    import monocle.std.char._
    ('x' |-> atBit(0) get)      shouldEqual false
    ('x' |-> atBit(0) set true) shouldEqual 'y'
  }

  // same for all other classes having a Bits instances


}
