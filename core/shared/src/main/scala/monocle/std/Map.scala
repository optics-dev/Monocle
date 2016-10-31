package monocle.std

import monocle.Iso

object map extends MapOptics

trait MapOptics {
  def mapToSet[K]: Iso[Map[K, Unit], Set[K]] =
    Iso[Map[K, Unit], Set[K]](_.keySet)(_.map(k => (k, ())).toMap)
}
