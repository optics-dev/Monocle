//Created By Ilan Godik
package monocle

import monocle.std.tuple._

object TupleExample extends App {

  println(_1[Int,String,Double].set((5,"Hello"), 7.3)) // (7.3,Hello)
  println(_2[String,Int,Char].set(("Hello",5),'H')) // (Hello,H)

}
