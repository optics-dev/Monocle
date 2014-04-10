package monocle.function


import monocle.{Traversal, SimpleTraversal}


trait Each[S, A] {

  /**
   * Creates a Traversal from a monomorphic container S to all of its elements
   */
  def each: SimpleTraversal[S, A]

}

object Each extends EachInstances


trait EachInstances {

  def each[S, A](implicit ev: Each[S, A]): SimpleTraversal[S, A] = ev.each

  implicit def mapEachInstance[K, V]: Each[Map[K, V], V] = new Each[Map[K, V], V] {

    import scalaz.std.map._

    def each: SimpleTraversal[Map[K, V], V] = Traversal[({type F[v] = Map[K,v]})#F, V, V]
  }

  implicit def optEachInstance[A]: Each[Option[A], A] = new Each[Option[A], A] {
    def each: SimpleTraversal[Option[A], A] = monocle.std.option.some
  }

  implicit def listEachInstance[A]: Each[List[A], A] = new Each[List[A], A] {

    import scalaz.std.list._

    def each: SimpleTraversal[List[A], A] = Traversal[List, A, A]
  }

  implicit def pairEachInstance[A]: Each[(A, A), A] = new Each[(A, A), A] {
    def each: SimpleTraversal[(A, A), A] =
      Traversal.apply2[(A, A), (A, A), A, A](_._1)(_._2)((_, b1, b2) => (b1, b2))
  }

  implicit def tripleEachInstance[A]: Each[(A, A, A), A] = new Each[(A, A, A), A] {
    def each: SimpleTraversal[(A, A, A), A] =
      Traversal.apply3[(A, A, A), (A, A, A), A, A](_._1)(_._2)(_._3)((_, b1, b2, b3) => (b1, b2, b3))
  }

}