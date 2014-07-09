package monocle.function


import monocle.syntax._
import monocle.{Traversal, SimpleTraversal}
import scalaz.Tree._
import scalaz.std.list._
import scalaz.std.map._
import scalaz.std.option._
import scalaz.std.stream._
import scalaz.std.vector._
import scalaz.{Applicative, IList, Traverse, Tree, OneAnd}


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
    def each = Traversal[S, A, A]
  }

  implicit def mapEach[K, V] = traverseEach[({type λ[α] = Map[K,α]})#λ, V]
  implicit def optEach[A]    = traverseEach[Option, A]
  implicit def listEach[A]   = traverseEach[List, A]
  implicit def iListEach[A]  = traverseEach[IList, A]
  implicit def streamEach[A] = traverseEach[Stream, A]
  implicit def vectorEach[A] = traverseEach[Vector, A]
  implicit def treeEach[A]   = traverseEach[Tree, A]
  implicit def oneAndEach[T[_]: Traverse, A] = traverseEach[({type λ[α] = OneAnd[T, α]})#λ, A]

  implicit val stringEach = new Each[String, Char] {
    def each = monocle.std.string.stringToList |->> listEach.each
  }

  implicit def someEach[A] = new Each[Some[A], A] {
    def each = monocle.std.option.someIso
  }

  implicit def pairEach[A] = new Each[(A, A), A] {
    def each =
      Traversal.apply2[(A, A), (A, A), A, A](_._1,_._2)((_, b1, b2) => (b1, b2))
  }

  implicit def tripleEach[A] = new Each[(A, A, A), A] {
    def each =
      Traversal.apply3[(A, A, A), (A, A, A), A, A](_._1,_._2,_._3)((_, b1, b2, b3) => (b1, b2, b3))
  }

  implicit def quadrupleEach[A] = new Each[(A, A, A, A), A] {
    def each =
      Traversal.apply4[(A, A, A, A), (A, A, A, A), A, A](_._1,_._2,_._3,_._4)((_, b1, b2, b3, b4) => (b1, b2, b3, b4))
  }

  implicit def quintupleEach[A] = new Each[(A, A, A, A, A), A] {
    def each =
      Traversal.apply5[(A, A, A, A, A), (A, A, A, A, A), A, A](_._1,_._2,_._3,_._4,_._5)((_, b1, b2, b3, b4, b5) => (b1, b2, b3, b4, b5))
  }

  implicit def sixtupleEach[A] = new Each[(A, A, A, A, A, A), A] {
    def each =
      Traversal.apply6[(A, A, A, A, A, A), (A, A, A, A, A, A), A, A](_._1,_._2,_._3,_._4,_._5, _._6)((_, b1, b2, b3, b4, b5, b6) => (b1, b2, b3, b4, b5, b6))
  }

}