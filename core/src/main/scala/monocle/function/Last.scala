package monocle.function

import monocle.SimpleTraversal
import monocle.syntax.traversal._
import scalaz.IList


trait Last[S, A] {

  /** Creates a Traversal from S to its optional last element */
  def last: SimpleTraversal[S, A]

}


object Last extends LastInstances

trait LastInstances {

  def last[S, A](implicit ev: Last[S, A]): SimpleTraversal[S, A] = ev.last

  def reverseHeadLast[S, A](implicit evHead: Head[S, A], evReverse: Reverse[S, S]): Last[S, A] = new Last[S, A] {
    def last: SimpleTraversal[S, A] = evReverse.reverse |->> evHead.head
  }

  implicit def listLast[A]  : Last[List[A]  , A]    = reverseHeadLast[List[A]  , A]
  implicit def iListLast[A] : Last[IList[A] , A]    = reverseHeadLast[IList[A] , A]
  implicit def streamLast[A]: Last[Stream[A], A]    = reverseHeadLast[Stream[A], A]
  implicit def vectorLast[A]: Last[Vector[A], A]    = reverseHeadLast[Vector[A]  , A]
  implicit val stringLast   : Last[String   , Char] = reverseHeadLast[String   , Char]


  implicit def optionLast[A]: Last[Option[A], A]    = new Last[Option[A], A] {
    def last: SimpleTraversal[Option[A], A] = monocle.std.option.some
  }

}