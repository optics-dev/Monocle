package monocle.function

import monocle.{Iso, Prism}

import cats.Order

import scala.annotation.implicitNotFound
import scala.collection.immutable.SortedMap

/**
 * Typeclass that defines a [[Prism]] from an `S` and its empty value
 * @tparam S source of [[Prism]]
 */
@implicitNotFound("Could not find an instance of Empty[${S}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Empty[S] extends Serializable {
  def empty: Prism[S, Unit]
}

trait EmptyFunctions {
  def empty[S](implicit ev: Empty[S]): Prism[S, Unit] =
    ev.empty

  def _isEmpty[S](s: S)(implicit ev: Empty[S]): Boolean =
    ev.empty.getOption(s).isDefined

  def _empty[S](implicit ev: Empty[S]): S =
    ev.empty.reverseGet(())
}

object Empty extends EmptyFunctions {
  /** lift an instance of [[Empty]] using an [[Iso]] */
  def fromIso[S, A](iso: Iso[S, A])(implicit ev: Empty[A]): Empty[S] = new Empty[S] {
    val empty: Prism[S, Unit] =
      iso composePrism ev.empty
  }

  /************************************************************************************************/
  /** Std instances                                                                               */
  /************************************************************************************************/

  implicit def listEmpty[A]: Empty[List[A]] = new Empty[List[A]] {
    val empty = Prism[List[A], Unit](l => if(l.isEmpty) Some(()) else None)(_ => List.empty)
  }

  implicit def sortedMapEmpty[K, V](implicit ok: Order[K]): Empty[SortedMap[K, V]] = new Empty[SortedMap[K, V]] {
    val empty = Prism[SortedMap[K, V], Unit](m => if(m.isEmpty) Some(()) else None)(_ => SortedMap.empty(ok.toOrdering))
  }

  implicit def optionEmpty[A]: Empty[Option[A]] = new Empty[Option[A]] {
    val empty = monocle.std.option.none[A]
  }

  implicit def emptySet[A]: Empty[Set[A]] = new Empty[Set[A]] {
    val empty = Prism[Set[A], Unit](s => if(s.isEmpty) Some(()) else None)(_ => Set.empty[A])
  }

  implicit def streamEmpty[A]: Empty[Stream[A]] = new Empty[Stream[A]] {
    val empty = Prism[Stream[A], Unit](s => if(s.isEmpty) Some(()) else None)(_ => Stream.empty)
  }

  implicit val stringEmpty: Empty[String] = new Empty[String] {
    val empty = Prism[String, Unit](s => if(s.isEmpty) Some(()) else None)(_ => "")
  }

  implicit def vectorEmpty[A]: Empty[Vector[A]] = new Empty[Vector[A]] {
    val empty = Prism[Vector[A], Unit](v => if(v.isEmpty) Some(()) else None)(_ => Vector.empty)
  }
}
