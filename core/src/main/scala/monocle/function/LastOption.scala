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

  def reverseHeadLastOption[S, A](implicit evReverse: Reverse[S, S], evHead: HeadOption[S, A]) = new LastOption[S, A] {
    def lastOption = evReverse.reverse composeOptional evHead.headOption
  }

  implicit def listLastOption[A]   = reverseHeadLastOption[List[A]  , A]
  implicit def iListLastOption[A]  = reverseHeadLastOption[IList[A] , A]
  implicit def streamLastOption[A] = reverseHeadLastOption[Stream[A], A]
  implicit def vectorLastOption[A] = reverseHeadLastOption[Vector[A], A]
  implicit val stringLastOption    = reverseHeadLastOption[String   , Char]

  implicit def oneAndLastOption[A, T[_]](implicit ev: LastOption[T[A], A]) = new LastOption[OneAnd[T, A], A] {
    def lastOption = SimpleOptional.build[OneAnd[T, A], A](oneAnd => ev.lastOption.getOption(oneAnd.tail),
      (oneAnd, a) => oneAnd.copy(tail = ev.lastOption.set(oneAnd.tail, a)))
  }

  implicit def optionLastOption[A] = new LastOption[Option[A], A] {
    def lastOption = monocle.std.option.some
  }

  implicit def someLastOption[A] = new LastOption[Some[A], A] {
    def lastOption = monocle.std.option.someIso
  }

}

