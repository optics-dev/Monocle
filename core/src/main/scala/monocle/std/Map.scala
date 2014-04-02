package monocle.std

import monocle._
import monocle.util.Each
import scala.Some

object map extends MapInstances

trait MapInstances {

  def at[K, V](key: K) = SimpleLens[Map[K, V], Option[V]](
    _.get(key),
    (map, optValue) => optValue match {
      case Some(value) => map + (key -> value)
      case None        => map - key
    })

  implicit def mapEachInstance[K, V]: Each.Aux[Map[K, V], V] = new Each[Map[K, V]] {
    import scalaz.std.map._
    type IN = V
    def each: SimpleTraversal[Map[K, V], V] = Traversal[({type F[v] = Map[K,v]})#F, V, V]
  }

}
