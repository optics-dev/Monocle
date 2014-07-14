package monocle.function

import monocle.{Traversal, SimpleTraversal}
import scalaz.Traverse


trait Each[S, A] {

  /**
   * Creates a Traversal from a monomorphic container S to all of its elements of type A
   */
  def each: SimpleTraversal[S, A]

}

object Each extends EachFunctions


trait EachFunctions {

  def each[S, A](implicit ev: Each[S, A]): SimpleTraversal[S, A] = ev.each

  def traverseEach[S[_]: Traverse, A]: Each[S[A], A] = new Each[S[A], A] {
    def each = Traversal[S, A, A]
  }

}