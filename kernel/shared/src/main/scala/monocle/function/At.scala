package monocle.function

import monocle.{Lens, Optional}
import monocle.Prism.some

trait At[S] extends Index[S] {

  def at(index: I): Lens[S, Option[A]]

  def index(index: I): Optional[S, A] =
    at(index) composePrism some
}

object At {
  type Aux[S, I0, A0] = At[S] { type I = I0; type A = A0 }

  def apply[S, I0, A0](f : I0 => Lens[S, Option[A0]]): Aux[S, I0, A0] =
    new At[S] {
      type I = I0
      type A = A0
      def at(index: I0): Lens[S, Option[A0]] = f(index)
    }

  implicit def map[K, V]: Aux[Map[K, V], K, V] =
    apply((key: K) =>
      Lens[Map[K, V], Option[V]](_.get(key))((map, optA) =>
        optA match {
          case None    => map - key
          case Some(a) => map + (key -> a)
        }
      )
    )

  implicit def set[A]: Aux[Set[A], A, Unit] =
    apply((key: A) =>
      Lens[Set[A], Option[Unit]](set => if(set.contains(key)) Some(()) else None){
        case (set, None) => set - key
        case (set, Some(_)) => set + key
      }
    )
}