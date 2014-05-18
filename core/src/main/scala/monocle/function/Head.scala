package monocle.function

import monocle.SimpleLens
import monocle.function.Field1._


trait Head[S, A] {

  /**
   * Creates a Lens from S to its first element
   * head is safe, it should only be implemented on object with a first element
   */
   def head: SimpleLens[S, A]

}


object Head extends HeadInstances

trait HeadInstances extends HeadInstances1 {

  def head[S, A](implicit ev: Head[S, A]): SimpleLens[S, A] = ev.head

}

trait HeadInstances1 {

  implicit def Field1Head[S, A](implicit ev: Field1[S,A]): Head[S, A] = new Head[S, A]{
    def head = _1
  }

}