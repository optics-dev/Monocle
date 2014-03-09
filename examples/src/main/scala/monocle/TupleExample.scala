package monocle

import monocle.std.tuple._

object TupleExample extends App {

  println( _1.set((5,"Hello"), 7.3) ) // (7.3,Hello)
  println( _1.get((4, "Hi")) )        // 4
  println( _2.set(("Hello",5),'H') )  // (Hello,H)

  println( both.set((3, 4), 'a') )              // (a, a)
  println( both.modify((3, 4), (_: Int) + 1) )  // (4, 5)

}
