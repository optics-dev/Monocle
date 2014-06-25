package monocle.function

import monocle.SimpleOptional
import monocle.function.Index._
import scala.collection.immutable.Stream.Empty
import scalaz.{ICons, INil, IList}


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

  implicit def listHeadOption[A] = new HeadOption[List[A], A] {
    def headOption = SimpleOptional.build[List[A], A](_.headOption, {
      case (Nil, a)     => Nil
      case (x :: xs, a) => a :: xs
    })
  }

  implicit def iListHeadOption[A] = new HeadOption[IList[A], A] {
    def headOption = SimpleOptional.build[IList[A], A](_.headOption, {
      case (INil(), a)      => INil[A]()
      case (ICons(x,xs), a) => ICons(a, xs)
    })
  }

  implicit def streamHeadOption[A] = new HeadOption[Stream[A], A] {
    def headOption = SimpleOptional.build[Stream[A], A](_.headOption, {
      case (Empty, a)    => Empty
      case (x #:: xs, a) => a #:: xs
    })
  }

  implicit def vectorHeadOption[A] = new HeadOption[Vector[A], A] {
    def headOption = SimpleOptional.build[Vector[A], A](_.headOption, (vector, a) =>
      if(vector.isEmpty) vector else a +: vector.tail
    )
  }

  implicit val stringHeadOption = new HeadOption[String, Char] {
    def headOption = monocle.std.string.stringToList composeOptional listHeadOption.headOption
  }

  implicit def optionHeadOption[A]: HeadOption[Option[A], A] = new HeadOption[Option[A], A] {
    def headOption = monocle.std.option.some
  }

}