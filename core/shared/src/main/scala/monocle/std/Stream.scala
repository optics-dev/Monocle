package monocle.std

import monocle.function._
import monocle.{Optional, Prism, Traversal}

import scala.collection.immutable.Stream.{#::, Empty}
import scalaz.Applicative
import scalaz.Id.Id
import scalaz.std.stream._
import scalaz.syntax.traverse._

object stream extends StreamOptics

trait StreamOptics {

  implicit def streamEmpty[A]: Empty[Stream[A]] = new Empty[Stream[A]] {
    def empty = Prism[Stream[A], Unit](s => if(s.isEmpty) Some(()) else None)(_ => Stream.empty)
  }

  implicit def streamEach[A]: Each[Stream[A], A] = Each.traverseEach[Stream, A]

  implicit def streamIndex[A]: Index[Stream[A], Int, A] = new Index[Stream[A], Int, A] {
    def index(i: Int) = Optional[Stream[A], A](
      s      => if(i < 0) None else s.drop(i).headOption)(
      a => s => s.zipWithIndex.traverse[Id, A]{
        case (_    , index) if index == i => a
        case (value, index)               => value
      }
    )
  }

  implicit def streamFilterIndex[A]: FilterIndex[Stream[A], Int, A] =
    FilterIndex.traverseFilterIndex[Stream, A](_.zipWithIndex)

  implicit def streamCons[A]: Cons[Stream[A], A] = new Cons[Stream[A], A]{
    def cons = Prism[Stream[A], (A, Stream[A])]{
      case Empty    => None
      case x #:: xs => Some((x, xs))
    }{ case (a, s) => a #:: s }
  }

  implicit def streamSnoc[A]: Snoc[Stream[A], A] = new Snoc[Stream[A], A]{
    def snoc = Prism[Stream[A], (Stream[A], A)]( s =>
      for {
        init <- if(s.isEmpty) None else Some(s.init)
        last <- s.lastOption
      } yield (init, last)){
      case (init, last) => init :+ last
    }
  }

  implicit def streamReverse[A]: Reverse[Stream[A], Stream[A]] =
    Reverse.reverseFromReverseFunction[Stream[A]](_.reverse)

  implicit def streamPlated[A]: Plated[Stream[A]] = new Plated[Stream[A]] {
    val plate: Traversal[Stream[A], Stream[A]] = new Traversal[Stream[A], Stream[A]] {
      def modifyF[F[_]: Applicative](f: Stream[A] => F[Stream[A]])(s: Stream[A]): F[Stream[A]] =
        s match {
          case x #:: xs => Applicative[F].map(f(xs))(x #:: _)
          case Stream() => Applicative[F].point(Stream.empty)
        }
    }
  }

}
