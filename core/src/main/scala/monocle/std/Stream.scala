package monocle.std

import monocle.function._
import monocle.{SimpleOptional, SimplePrism}

import scala.collection.immutable.Stream.{#::, Empty}
import scalaz.std.stream._

object stream extends StreamInstances

trait StreamInstances {

  implicit def streamEmpty[A]: Empty[Stream[A]] = new Empty[Stream[A]] {
    def empty = SimplePrism[Stream[A], Unit](s => if(s.isEmpty) Some(()) else None, _ => Stream.empty)
  }

  implicit def streamEach[A]: Each[Stream[A], A] = Each.traverseEach[Stream, A]

  implicit def streamIndex[A]: Index[Stream[A], Int, A] =
    Index.traverseIndex[Stream, A](_.zipWithIndex)

  implicit def streamFilterIndex[A]: FilterIndex[Stream[A], Int, A] =
    FilterIndex.traverseFilterIndex[Stream, A](_.zipWithIndex)

  implicit def streamCons[A]: Cons[Stream[A], A] = new Cons[Stream[A], A]{
    def _cons = SimplePrism[Stream[A], (A, Stream[A])]({
      case Empty    => None
      case x #:: xs => Some(x, xs)
    }, { case (a, s) => a #:: s })
  }

  implicit def streamSnoc[A]: Snoc[Stream[A], A] = new Snoc[Stream[A], A]{
    def snoc = SimplePrism[Stream[A], (Stream[A], A)]( s =>
      for {
        init <- if(s.isEmpty) None else Some(s.init)
        last <- if(s.isEmpty) None else Some(s.last)
      } yield (init, last),
    { case (init, last) => init :+ last }
    )
  }

  implicit def streamHeadOption[A]: HeadOption[Stream[A], A] =
    HeadOption.consHeadOption[Stream[A], A]

  implicit def streamTailOption[A]: TailOption[Stream[A], Stream[A]] =
    TailOption.consTailOption[Stream[A], A]

  implicit def streamLastOption[A]: LastOption[Stream[A], A] =
    LastOption.snocLastOption[Stream[A], A]

  implicit def streamInitOption[A]: InitOption[Stream[A], Stream[A]] =
    InitOption.snocInitOption[Stream[A], A]

  implicit def streamReverse[A]: Reverse[Stream[A], Stream[A]] =
    reverseFromReverseFunction[Stream[A]](_.reverse)

}
