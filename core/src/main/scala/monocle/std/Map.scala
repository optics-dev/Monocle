package monocle.std

import monocle.Lens

object Map {

  def at[K,V](key: K)= Lens[Map[K,V], Map[K,V], Option[V], Option[V]](
    _.get(key),
    (map, optValue) => optValue match {
      case Some(value) => map + (key -> value)
      case None        => map - key
    }
  )

}
