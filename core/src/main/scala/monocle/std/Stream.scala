package monocle.std

import monocle.function._
import monocle.{SimpleOptional, SimplePrism}

import scala.collection.immutable.Stream.{#::, Empty}
import scalaz.Id.Id
import scalaz.Maybe
import scalaz.std.stream._
import scalaz.syntax.traverse._

object stream extends StreamInstances

trait StreamInstances {

  implicit def streamEmpty[A]: Empty[Stream[A]] = new Empty[Stream[A]] {
    def empty = SimplePrism[Stream[A], Unit](s => if(s.isEmpty) Maybe.just(()) else Maybe.empty, _ => Stream.empty)
  }

  implicit def streamEach[A]: Each[Stream[A], A] = Each.traverseEach[Stream, A]

  implicit def streamIndex[A]: Index[Stream[A], Int, A] = new Index[Stream[A], Int, A] {
    def index(i: Int) = SimpleOptional[Stream[A], A](
      s      => if(i < 0) Maybe.empty else Maybe.optionMaybeIso.to(s.drop(i).headOption),
      (a, s) => s.zipWithIndex.traverse[Id, A]{
        case (_    , index) if index == i => a
        case (value, index)               => value
      }
    )
  }

  implicit def streamFilterIndex[A]: FilterIndex[Stream[A], Int, A] =
    FilterIndex.traverseFilterIndex[Stream, A](_.zipWithIndex)

  implicit def streamCons[A]: Cons[Stream[A], A] = new Cons[Stream[A], A]{
    def _cons = SimplePrism[Stream[A], (A, Stream[A])]({
      case Empty    => Maybe.empty
      case x #:: xs => Maybe.just((x, xs))
    }, { case (a, s) => a #:: s })
  }

  implicit def streamSnoc[A]: Snoc[Stream[A], A] = new Snoc[Stream[A], A]{
    def snoc = SimplePrism[Stream[A], (Stream[A], A)]( s =>
      for {
        init <- if(s.isEmpty) Maybe.empty else Maybe.just(s.init)
        last <- if(s.isEmpty) Maybe.empty else Maybe.just(s.last)
      } yield (init, last),
    { case (init, last) => init :+ last }
    )
  }

  implicit def streamReverse[A]: Reverse[Stream[A], Stream[A]] =
    reverseFromReverseFunction[Stream[A]](_.reverse)

}
