package monocle.function


import monocle.syntax.traversal._
import monocle.{Traversal, SimpleTraversal}
import scalaz.Tree


trait Each[S, A] {

  /**
   * Creates a Traversal from a monomorphic container S to all of its elements
   */
  def each: SimpleTraversal[S, A]

}

object Each extends EachInstances


trait EachInstances {

  def each[S, A](implicit ev: Each[S, A]): SimpleTraversal[S, A] = ev.each

  implicit def mapEach[K, V]: Each[Map[K, V], V] = new Each[Map[K, V], V] {

    import scalaz.std.map._

    def each: SimpleTraversal[Map[K, V], V] = Traversal[({type F[v] = Map[K,v]})#F, V, V]
  }

  implicit def optEach[A]: Each[Option[A], A] = new Each[Option[A], A] {
    def each: SimpleTraversal[Option[A], A] = monocle.std.option.some
  }

  implicit def listEach[A]: Each[List[A], A] = new Each[List[A], A] {

    import scalaz.std.list._

    def each: SimpleTraversal[List[A], A] = Traversal[List, A, A]
  }

  implicit def streamEach[A]: Each[Stream[A], A] = new Each[Stream[A], A] {

    import scalaz.std.stream._

    def each: SimpleTraversal[Stream[A], A] = Traversal[Stream, A, A]
  }

  implicit val stringEach: Each[String, Char] = new Each[String, Char] {
    def each: SimpleTraversal[String, Char] = monocle.std.string.stringToList |->> listEach.each
  }

  implicit def pairEach[A]: Each[(A, A), A] = new Each[(A, A), A] {
    def each: SimpleTraversal[(A, A), A] =
      Traversal.apply2[(A, A), (A, A), A, A](_._1)(_._2)((_, b1, b2) => (b1, b2))
  }

  implicit def tripleEach[A]: Each[(A, A, A), A] = new Each[(A, A, A), A] {
    def each: SimpleTraversal[(A, A, A), A] =
      Traversal.apply3[(A, A, A), (A, A, A), A, A](_._1)(_._2)(_._3)((_, b1, b2, b3) => (b1, b2, b3))
  }

  implicit def treeEach[A]: Each[Tree[A], A] = new Each[Tree[A], A] {

    import scalaz.Tree._

    def each: SimpleTraversal[Tree[A], A] = Traversal[Tree, A, A]
  }

  implicit def vectorEachInstance[A]: Each[Vector[A], A] = new Each[Vector[A], A] {

    import scalaz.std.vector._

    def each: SimpleTraversal[Vector[A], A] = Traversal[Vector, A, A]
  }

}