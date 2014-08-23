package monocle.std

import monocle.function._
import monocle.{SimplePrism, Optional, SimpleOptional}
import scala.collection.immutable.Stream.{#::, Empty}
import scalaz.Applicative
import scalaz.std.stream._

object stream extends StreamInstances

trait StreamInstances {

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

  implicit def streamSnoc[A]: Snoc[Stream[A], A] = Snoc.fromReverseCons

  implicit def streamHeadOption[A]: HeadOption[Stream[A], A] = new HeadOption[Stream[A], A] {
    def headOption = SimpleOptional[Stream[A], A](_.headOption, {
      case (Empty, a)    => Empty
      case (x #:: xs, a) => a #:: xs
    })
  }

  implicit def streamTailOption[A]: TailOption[Stream[A], Stream[A]] = new TailOption[Stream[A], Stream[A]]{
    def tailOption = new Optional[Stream[A], Stream[A], Stream[A], Stream[A]] {
      def multiLift[F[_] : Applicative](from: Stream[A], f: Stream[A] => F[Stream[A]]): F[Stream[A]] = from match {
        case Empty    => Applicative[F].point(Empty)
        case x #:: xs => Applicative[F].map(f(xs))(x #:: _)
      }
    }
  }

  implicit def streamLastOption[A]: LastOption[Stream[A], A] =
    LastOption.reverseHeadLastOption[Stream[A], A]

  implicit def streamInitOption[A]: InitOption[Stream[A], Stream[A]] =
    InitOption.reverseTailInitOption[Stream[A]]

  implicit def streamReverse[A]: Reverse[Stream[A], Stream[A]] =
    Reverse.simple[Stream[A]](_.reverse)



}
