package monocle

import scalaz.Equal

object TestHelper {

  def defaultEqual[A]: Equal[A] = new Equal[A] {
    def equal(a1: A, a2: A): Boolean = a1 equals a2
  }

}
