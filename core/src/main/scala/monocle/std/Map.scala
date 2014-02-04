package monocle.std

import monocle.SLens


object Map {

  def at[K,V](key: K): SLens[Map[K,V], Option[V]] = SLens[Map[K,V], Option[V]](
    _.get(key),
    (map, optValue) => optValue match {
      case Some(value) => map + (key -> value)
      case None        => map - key
    }
  )

}
