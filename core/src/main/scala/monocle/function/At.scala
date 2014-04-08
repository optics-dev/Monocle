package monocle.function

import monocle.{SimpleTraversal, SimpleLens}

trait At[S, I, A] {

  /** Creates a Lens from S to an optional A at index i */
  def at(i: I): SimpleLens[S, Option[A]]

  /** Creates a Traversal from S to all optional A with an index matching the predicate */
  def filterAt(predicate: I => Boolean): SimpleTraversal[S, Option[A]]

}


object At {

  def at[S, I, A](i: I)(implicit ev: At[S, I, A]): SimpleLens[S, Option[A]] = ev.at(i)

  def filterAt[S, I, A](predicate: I => Boolean)
                       (implicit ev: At[S, I, A]): SimpleTraversal[S, Option[A]] = ev.filterAt(predicate)

  implicit def atMap[K, V] = new At[Map[K, V], K, V]{
    def at(i: K) = SimpleLens[Map[K, V], Option[V]](
      _.get(i),
      (map, optValue) => optValue match {
        case Some(value) => map + (i -> value)
        case None        => map - i
      })

    def filterAt(predicate: K => Boolean): SimpleTraversal[Map[K, V], Option[V]] = ???
  }

}
