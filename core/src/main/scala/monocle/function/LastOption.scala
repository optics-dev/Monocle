package monocle.function

import monocle.SimpleOptional
import monocle.function.HeadOption._
import monocle.function.Reverse._
import scalaz.{OneAnd, IList}

trait LastOption[S, A] {

  /** Creates a Traversal from S to its optional last element */
  def lastOption: SimpleOptional[S, A]

}


object LastOption extends LastOptionInstances

trait LastOptionInstances {

  def lastOption[S, A](implicit ev: LastOption[S, A]): SimpleOptional[S, A] = ev.lastOption

  def reverseHeadLast[S, A](implicit evReverse: Reverse[S, S], evHead: HeadOption[S, A]): LastOption[S, A] = new LastOption[S, A] {
    def lastOption = evReverse.reverse composeOptional evHead.headOption
  }

  implicit def listLast[A]  : LastOption[List[A]  , A]    = reverseHeadLast[List[A]  , A]
  implicit def iListLast[A] : LastOption[IList[A] , A]    = reverseHeadLast[IList[A] , A]
  implicit def streamLast[A]: LastOption[Stream[A], A]    = reverseHeadLast[Stream[A], A]
  implicit def vectorLast[A]: LastOption[Vector[A], A]    = reverseHeadLast[Vector[A], A]
  implicit val stringLast   : LastOption[String   , Char] = reverseHeadLast[String   , Char]

  implicit def oneAndLast[A, T[_]](implicit ev: LastOption[T[A], A]) = new LastOption[OneAnd[T, A], A] {
    def lastOption = SimpleOptional.build[OneAnd[T, A], A](oneAnd => ev.lastOption.getOption(oneAnd.tail),
      (oneAnd, a) => oneAnd.copy(tail = ev.lastOption.set(oneAnd.tail, a)))
  }

  implicit def optionLast[A]: LastOption[Option[A], A]  = new LastOption[Option[A], A] {
    def lastOption = monocle.std.option.some
  }

}

