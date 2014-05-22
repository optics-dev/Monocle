package monocle.function

import monocle.SimpleTraversal
import monocle.function.Index._
import scalaz.IList


trait HeadOption[S, A] {

  /** Creates a Traversal from S to its optional first element */
  def headOption: SimpleTraversal[S, A]

}


object HeadOption extends HeadOptionInstances

trait HeadOptionInstances {

  def headOption[S, A](implicit ev: HeadOption[S, A]): SimpleTraversal[S, A] = ev.headOption

  def indexHeadOption[S, A](implicit ev: Index[S, Int, A]): HeadOption[S, A] = new HeadOption[S, A] {
    def headOption: SimpleTraversal[S, A] = index(0)
  }

  implicit def listHeadOption[A]  : HeadOption[List[A]  , A]    = indexHeadOption[List[A]  , A]
  implicit def iListHeadOption[A] : HeadOption[IList[A] , A]    = indexHeadOption[IList[A] , A]
  implicit def streamHeadOption[A]: HeadOption[Stream[A], A]    = indexHeadOption[Stream[A], A]
  implicit def vectorHeadOption[A]: HeadOption[Vector[A], A]    = indexHeadOption[Vector[A]  , A]
  implicit val stringHeadOption   : HeadOption[String   , Char] = indexHeadOption[String   , Char]

  implicit def optionHeadOption[A]: HeadOption[Option[A], A]    = new HeadOption[Option[A], A] {
    def headOption: SimpleTraversal[Option[A], A] = monocle.std.option.some
  }

}