package monocle.function

import monocle.Lens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Head[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Head[S, A] {

  /**
   * Creates a Lens from S to its first element
   * head is safe, it should only be implemented on object with a first element
   */
   def head: Lens[S, A]

}


object Head extends HeadFunctions

trait HeadFunctions {

  def head[S, A](implicit ev: Head[S, A]): Lens[S, A] = ev.head

  def field1Head[S, A](implicit ev: Field1[S,A]): Head[S, A] = new Head[S, A]{
    def head = ev.first
  }

}