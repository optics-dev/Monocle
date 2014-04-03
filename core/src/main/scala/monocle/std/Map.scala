package monocle.std

import monocle._
import scala.Some

object map extends MapInstances

trait MapInstances {

  def at[K, V](key: K) = SimpleLens[Map[K, V], Option[V]](
    _.get(key),
    (map, optValue) => optValue match {
      case Some(value) => map + (key -> value)
      case None        => map - key
    })

}
