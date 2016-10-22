package monocle.function

import monocle.{Iso, Prism}

import scala.annotation.implicitNotFound

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

  implicit def mapEmpty[K, V]: Empty[Map[K, V]] = new Empty[Map[K, V]] {
    val empty = Prism[Map[K, V], Unit](m => if(m.isEmpty) Some(()) else None)(_ => Map.empty)
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

  /************************************************************************************************/
  /** Scalaz instances                                                                            */
  /************************************************************************************************/
  import monocle.std.maybe.nothing
  import scalaz.{==>>, IList, ISet, Maybe}

  implicit def iListEmpty[A]: Empty[IList[A]] = new Empty[IList[A]] {
    def empty = Prism[IList[A], Unit](l => if(l.isEmpty) Some(()) else None)(_ => IList.empty)
  }

  implicit def iMapEmpty[K, V]: Empty[K ==>> V] = new Empty[K ==>> V] {
    def empty = Prism[K ==>> V, Unit](m => if(m.isEmpty) Some(()) else None)(_ => ==>>.empty)
  }

  implicit def emptyISet[A]: Empty[ISet[A]] = new Empty[ISet[A]] {
    def empty = Prism[ISet[A], Unit](s => if(s.isEmpty) Some(()) else None)(_ => ISet.empty[A])
  }

  implicit def maybeEmpty[A]: Empty[Maybe[A]] = new Empty[Maybe[A]]{
    def empty = nothing
  }
}