package lens.std

import lens.Lens
import lens.impl.HLens


object Map {

  def at[K,V](key: K) : Lens[Map[K,V], Option[V]] = {
    HLens[Map[K,V], Option[V]](
      _.get(key),
      (map, optValue) => optValue match {
        case Some(value) => map + (key -> value)
        case None        => map - key
    })
  }

}
