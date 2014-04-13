package monocle.function

import monocle.SimpleLens

trait At[S, I, A] {

  /**
   * Creates a Lens from S to an optional A at index i
   * at is strictly more powerful than index because we can add a value at an empty index
   */
  def at(i: I): SimpleLens[S, Option[A]]

}


object At extends AtInstances

trait AtInstances {

  def at[S, I, A](i: I)(implicit ev: At[S, I, A]): SimpleLens[S, Option[A]] = ev.at(i)


  implicit def atMap[K, V] = new At[Map[K, V], K, V]{
    def at(i: K) = SimpleLens[Map[K, V], Option[V]](
      _.get(i),
      (map, optValue) => optValue match {
        case Some(value) => map + (i -> value)
        case None        => map - i
      })
  }

}
