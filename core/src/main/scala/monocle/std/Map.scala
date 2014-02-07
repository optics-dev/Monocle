package monocle.std

import monocle.SimpleLens


object Map {

  def at[K,V](key: K): SimpleLens[Map[K,V], Option[V]] = SimpleLens[Map[K,V], Option[V]](
    _.get(key),
    (map, optValue) => optValue match {
      case Some(value) => map + (key -> value)
      case None        => map - key
    }
  )

}
