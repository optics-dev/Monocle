package monocle.function

import monocle.SimpleOptional
import monocle.function.Index._
import scalaz.IList


trait HeadOption[S, A] {

  /** Creates a Traversal from S to its optional first element */
  def headOption: SimpleOptional[S, A]

}


object HeadOption extends HeadOptionInstances

trait HeadOptionInstances {

  def headOption[S, A](implicit ev: HeadOption[S, A]): SimpleOptional[S, A] = ev.headOption

  def indexHeadOption[S, A](implicit ev: Index[S, Int, A]): HeadOption[S, A] = new HeadOption[S, A] {
    def headOption = index(0)
  }

  implicit def listHeadOption[A]  : HeadOption[List[A]  , A]    = indexHeadOption[List[A]  , A]
  implicit def iListHeadOption[A] : HeadOption[IList[A] , A]    = indexHeadOption[IList[A] , A]
  implicit def streamHeadOption[A]: HeadOption[Stream[A], A]    = indexHeadOption[Stream[A], A]
  implicit def vectorHeadOption[A]: HeadOption[Vector[A], A]    = indexHeadOption[Vector[A]  , A]
  implicit val stringHeadOption   : HeadOption[String   , Char] = indexHeadOption[String   , Char]

  implicit def optionHeadOption[A]: HeadOption[Option[A], A]    = new HeadOption[Option[A], A] {
    def headOption = monocle.std.option.some
  }

}