package monocle.function

import cats.Order
import monocle.{Iso, Prism}

import scala.annotation.implicitNotFound
import scala.collection.immutable.SortedMap

/** Typeclass that defines a [[Prism]] from an `S` and its empty value
  * @tparam S source of [[Prism]]
  */
@implicitNotFound(
  "Could not find an instance of Empty[${S}], please check Monocle instance location policy to " + "find out which import is necessary"
)
@deprecated("no replacement", since = "3.0.0-M1")
abstract class Empty[S] extends Serializable {
  def empty: Prism[S, Unit]
}

trait EmptyFunctions {
  @deprecated("no replacement", since = "3.0.0-M1")
  def empty[S](implicit ev: Empty[S]): Prism[S, Unit] =
    ev.empty

  @deprecated("no replacement", since = "3.0.0-M1")
  def _isEmpty[S](s: S)(implicit ev: Empty[S]): Boolean =
    ev.empty.getOption(s).isDefined

  @deprecated("no replacement", since = "3.0.0-M1")
  def _empty[S](implicit ev: Empty[S]): S =
    ev.empty.reverseGet(())
}

object Empty extends EmptyFunctions {
  def apply[S](prism: Prism[S, Unit]): Empty[S] =
    new Empty[S] {
      override val empty: Prism[S, Unit] = prism
    }

  /** lift an instance of [[Empty]] using an [[Iso]] */
  def fromIso[S, A](iso: Iso[S, A])(implicit ev: Empty[A]): Empty[S] =
    Empty(
      iso.andThen(ev.empty)
    )

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */
  implicit def listEmpty[A]: Empty[List[A]] =
    Empty(
      Prism[List[A], Unit](l => if (l.isEmpty) Some(()) else None)(_ => List.empty)
    )

  implicit def lazyListEmpty[A]: Empty[LazyList[A]] =
    Empty(
      Prism[LazyList[A], Unit](s => if (s.isEmpty) Some(()) else None)(_ => LazyList.empty)
    )

  implicit def mapEmpty[K, V]: Empty[Map[K, V]] =
    Empty(
      Prism[Map[K, V], Unit](m => if (m.isEmpty) Some(()) else None)(_ => Map.empty)
    )

  implicit def sortedMapEmpty[K, V](implicit ok: Order[K]): Empty[SortedMap[K, V]] =
    Empty(
      Prism[SortedMap[K, V], Unit](m => if (m.isEmpty) Some(()) else None)(_ => SortedMap.empty(ok.toOrdering))
    )

  implicit def optionEmpty[A]: Empty[Option[A]] =
    Empty(
      monocle.std.option.none[A]
    )

  implicit def emptySet[A]: Empty[Set[A]] =
    Empty(
      Prism[Set[A], Unit](s => if (s.isEmpty) Some(()) else None)(_ => Set.empty[A])
    )

  implicit val stringEmpty: Empty[String] = Empty(
    Prism[String, Unit](s => if (s.isEmpty) Some(()) else None)(_ => "")
  )

  implicit def vectorEmpty[A]: Empty[Vector[A]] =
    Empty(
      Prism[Vector[A], Unit](v => if (v.isEmpty) Some(()) else None)(_ => Vector.empty)
    )

  /** *********************************************************************************************
    */
  /** Cats instances */
  /** *********************************************************************************************
    */
  import cats.data.Chain

  implicit def chainEmpty[A]: Empty[Chain[A]] =
    Empty(
      Prism[Chain[A], Unit](l => if (l.isEmpty) Some(()) else None)(_ => Chain.empty)
    )
}
