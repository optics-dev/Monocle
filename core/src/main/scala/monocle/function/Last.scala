package monocle.function

import monocle.SimpleLens
import scala.collection.immutable.Stream.Empty


trait Last[S, A] {

  /** Creates a Lens from S to its optional last element */
  def last: SimpleLens[S, Option[A]]

}


object Last extends LastInstances

trait LastInstances {

  def last[S, A](implicit ev: Last[S, A]): SimpleLens[S, Option[A]] = ev.last

  implicit def listLast[A] = new Last[List[A], A] {
    def last = SimpleLens[List[A], Option[A]](_.lastOption, {
      case(Nil, None  )  => Nil
      case(Nil, Some(a)) => a :: Nil
      case(xs , None  )  => xs.init
      case(xs , Some(a)) => xs.init ++ List(a)
    })
  }

  implicit def streamLast[A] = new Last[Stream[A], A] {
    def last = SimpleLens[Stream[A], Option[A]](_.lastOption, {
      case(Empty, None   ) => Empty
      case(Empty, Some(a)) => Stream(a)
      case(xs   , None   ) => xs.init
      case(xs   , Some(a)) => xs.init ++ Stream(a)
    })
  }

}