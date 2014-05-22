package monocle.function

import monocle.SimpleTraversal
import monocle.function.HeadOption._
import monocle.function.Reverse._
import monocle.syntax._
import scalaz.IList

trait LastOption[S, A] {

  /** Creates a Traversal from S to its optional last element */
  def lastOption: SimpleTraversal[S, A]

}


object LastOption extends LastOptionInstances

trait LastOptionInstances {

  def lastOption[S, A](implicit ev: LastOption[S, A]): SimpleTraversal[S, A] = ev.lastOption

  def reverseHeadLast[S, A](implicit evReverse: Reverse[S, S], evHead: HeadOption[S, A]): LastOption[S, A] = new LastOption[S, A] {
    def lastOption = evReverse.reverse |->> evHead.headOption
  }

  implicit def listLast[A]  : LastOption[List[A]  , A]    = reverseHeadLast[List[A]  , A]
  implicit def iListLast[A] : LastOption[IList[A] , A]    = reverseHeadLast[IList[A] , A]
  implicit def streamLast[A]: LastOption[Stream[A], A]    = reverseHeadLast[Stream[A], A]
  implicit def vectorLast[A]: LastOption[Vector[A], A]    = reverseHeadLast[Vector[A], A]
  implicit val stringLast   : LastOption[String   , Char] = reverseHeadLast[String   , Char]


  implicit def optionLast[A]: LastOption[Option[A], A]    = new LastOption[Option[A], A] {
    def lastOption: SimpleTraversal[Option[A], A] = monocle.std.option.some
  }

}

