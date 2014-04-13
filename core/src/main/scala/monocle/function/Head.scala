package monocle.function

import monocle.SimpleLens
import scala.collection.immutable.Stream.Empty


trait Head[S, A] {

  def head: SimpleLens[S, Option[A]]

}


object Head extends HeadInstances

trait HeadInstances {

  def head[S, A](implicit ev: Head[S, A]): SimpleLens[S, Option[A]] = ev.head

  implicit def listHead[A] = new Head[List[A], A] {
    def head = SimpleLens[List[A], Option[A]](_.headOption, {
      case(Nil    , None   ) => Nil
      case(Nil    , Some(a)) => List(a)
      case(x :: xs, None   ) => xs
      case(x :: xs, Some(a)) => a :: xs
    })
  }

  implicit def streamHead[A] = new Head[Stream[A], A] {
    def head = SimpleLens[Stream[A], Option[A]](_.headOption, {
      case(Empty   , None   ) => Empty
      case(Empty   , Some(a)) => Stream(a)
      case(x #:: xs, None   ) => xs
      case(x #::xs , Some(a)) => a #:: xs
    })
  }

}