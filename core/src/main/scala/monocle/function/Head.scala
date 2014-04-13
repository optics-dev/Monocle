package monocle.function

import monocle.SimpleTraversal


trait Head[S, A] {

  /** Creates a Traversal from S to its optional first element */
  def head: SimpleTraversal[S, A]

}


object Head extends HeadInstances

trait HeadInstances {

  def head[S, A](implicit ev: Head[S, A]): SimpleTraversal[S, A] = ev.head

  def indexHead[S, A](implicit ev: Index[S, Int, A]): Head[S, A] = new Head[S, A] {
    def head: SimpleTraversal[S, A] = ev.index(0)
  }

  implicit def listHead[A]  : Head[List[A]  , A]    = indexHead[List[A]  , A]
  implicit def streamHead[A]: Head[Stream[A], A]    = indexHead[Stream[A], A]
  implicit val stringHead   : Head[String   , Char] = indexHead[String   , Char]

}