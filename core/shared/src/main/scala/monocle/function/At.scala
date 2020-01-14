package monocle.function

import monocle.{Lens, Optional}
import monocle.Prism.some

trait At[From] extends Index[From] {
  def at(index: Index): Lens[From, Option[To]]

  def index(index: Index): Optional[From, To] =
    at(index) andThenPrism some
}

object At {
  type Aux[From, _Index, _To] = At[From] { type Index = _Index; type To = _To }

  def apply[From, _Index, _To](f: _Index => Lens[From, Option[_To]]): Aux[From, _Index, _To] =
    new At[From] {
      type Index = _Index
      type To    = _To
      def at(index: Index): Lens[From, Option[To]] = f(index)
    }

  implicit def map[K, V]: Aux[Map[K, V], K, V] =
    apply(
      (key: K) =>
        Lens[Map[K, V], Option[V]](_.get(key))(
          (map, optA) =>
            optA match {
              case None    => map - key
              case Some(a) => map + (key -> a)
            }
        )
    )

  implicit def set[A]: Aux[Set[A], A, Unit] =
    apply(
      (key: A) =>
        Lens[Set[A], Option[Unit]](set => if (set.contains(key)) Some(()) else None) {
          case (set, None)    => set - key
          case (set, Some(_)) => set + key
        }
    )
}
