package monocle.function

import monocle.{Lens, Optional}
import monocle.Prism.some

trait At[A] extends Index[A] {
  def at(index: I): Lens[A, Option[B]]

  def index(index: I): Optional[A, B] =
    at(index) compose some[B]
}

object At {
  type Aux[A, I0, B0] = At[A] { type I = I0; type B = B0 }

  def apply[A, I0, B0](f: I0 => Lens[A, Option[B0]]): Aux[A, I0, B0] =
    new At[A] {
      type I = I0
      type B = B0
      def at(index: I0): Lens[A, Option[B0]] = f(index)
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
