package monocle

import monocle.util.Bits._
import monocle.std.char._
import monocle.std.int._

object BitsExample extends App {

  val atFirstBit = atBit[Int](0)

  println(atFirstBit.get(3)) // true  i.e. 1
  println(atFirstBit.get(32)) // false i.e. 0

  println(atFirstBit.set(32, true)) // 33

  val atFirstBitForChar = atBit[Char](0)

  println(atFirstBitForChar.get('x'))       // false
  println(atFirstBitForChar.set('x', true)) // y

  import monocle.syntax.lens._

  println( 3 |-> atBit(1) get )        // true
  println( 3 |-> atBit(1) modify(!_) ) // 1

}
