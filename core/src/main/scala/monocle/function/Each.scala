package monocle.function


import monocle.syntax.traversal._
import monocle.{Traversal, SimpleTraversal}
import scalaz.Tree._
import scalaz.std.list._
import scalaz.std.map._
import scalaz.std.option._
import scalaz.std.stream._
import scalaz.std.vector._
import scalaz.{IList, Traverse, Tree}


trait Each[S, A] {

  /**
   * Creates a Traversal from a monomorphic container S to all of its elements of type A
   */
  def each: SimpleTraversal[S, A]

}

object Each extends EachInstances


trait EachInstances {

  def each[S, A](implicit ev: Each[S, A]): SimpleTraversal[S, A] = ev.each

  implicit def traverseEach[S[_]: Traverse, A]: Each[S[A], A] = new Each[S[A], A] {
    def each: SimpleTraversal[S[A], A] = Traversal[S, A, A]
  }

  implicit def mapEach[K, V]: Each[Map[K, V], V] = traverseEach[({type F[v] = Map[K,v]})#F, V]
  implicit def optEach[A]   : Each[Option[A], A] = traverseEach[Option, A]
  implicit def listEach[A]  : Each[List[A]  , A] = traverseEach[List, A]
  implicit def iListEach[A] : Each[IList[A] , A] = traverseEach[IList, A]
  implicit def streamEach[A]: Each[Stream[A], A] = traverseEach[Stream, A]
  implicit def vectorEach[A]: Each[Vector[A], A] = traverseEach[Vector, A]
  implicit def treeEach[A]  : Each[Tree[A]  , A] = traverseEach[Tree, A]

  implicit val stringEach: Each[String, Char] = new Each[String, Char] {
    def each: SimpleTraversal[String, Char] = monocle.std.string.stringToList |->> listEach.each
  }

  implicit def pairEach[A]: Each[(A, A), A] = new Each[(A, A), A] {
    def each: SimpleTraversal[(A, A), A] =
      Traversal.apply2[(A, A), (A, A), A, A](_._1,_._2)((_, b1, b2) => (b1, b2))
  }

  implicit def tripleEach[A]: Each[(A, A, A), A] = new Each[(A, A, A), A] {
    def each: SimpleTraversal[(A, A, A), A] =
      Traversal.apply3[(A, A, A), (A, A, A), A, A](_._1,_._2,_._3)((_, b1, b2, b3) => (b1, b2, b3))
  }

  implicit def quadrupleEach[A]: Each[(A, A, A, A), A] = new Each[(A, A, A, A), A] {
    def each: SimpleTraversal[(A, A, A, A), A] =
      Traversal.apply4[(A, A, A, A), (A, A, A, A), A, A](_._1,_._2,_._3,_._4)((_, b1, b2, b3, b4) => (b1, b2, b3, b4))
  }

  implicit def quintupleEach[A]: Each[(A, A, A, A, A), A] = new Each[(A, A, A, A, A), A] {
    def each: SimpleTraversal[(A, A, A, A, A), A] =
      Traversal.apply5[(A, A, A, A, A), (A, A, A, A, A), A, A](_._1,_._2,_._3,_._4,_._5)((_, b1, b2, b3, b4, b5) => (b1, b2, b3, b4, b5))
  }

  implicit def sixtupleEach[A]: Each[(A, A, A, A, A, A), A] = new Each[(A, A, A, A, A, A), A] {
    def each: SimpleTraversal[(A, A, A, A, A, A), A] =
      Traversal.apply6[(A, A, A, A, A, A), (A, A, A, A, A, A), A, A](_._1,_._2,_._3,_._4,_._5, _._6)((_, b1, b2, b3, b4, b5, b6) => (b1, b2, b3, b4, b5, b6))
  }

}