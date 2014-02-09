package monocle

import monocle.bits._


object BitsExample extends App {

  val atFirstBit = atBit[Int](0)

  println( atFirstBit.get(3) ) // true  i.e. 1
  println( atFirstBit.get(32)) // false i.e. 0

  println( atFirstBit.set(32, true) ) // 33

}
