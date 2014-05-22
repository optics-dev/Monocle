package monocle.function

import monocle.SimpleTraversal
import monocle.syntax._
import scalaz.syntax.equal._
import scalaz.{IList, Equal}

trait Index[S, I, A] {

  /**
   * Creates a Traversal from S to 0 or 1 A
   * index is less powerful than at as we can only modify an index that already exist
   */
  def index(i: I): SimpleTraversal[S, A]

}

object Index extends IndexInstances

trait IndexInstances {

  def index[S, I, A](i: I)(implicit ev: Index[S, I, A]): SimpleTraversal[S, A] = ev.index(i)

  implicit val equalInt = Equal.equalA[Int]

  implicit def mapIndex[K, V]: Index[Map[K, V], K  , V]    = atIndex
  implicit def listIndex[A]  : Index[List[A]  , Int, A]    = filterIndexIndex
  implicit def streamIndex[A]: Index[Stream[A], Int, A]    = filterIndexIndex
  implicit val stringIndex   : Index[String   , Int, Char] = filterIndexIndex
  implicit def vectorIndex[A]: Index[Vector[A], Int, A]    = filterIndexIndex
  implicit def iListIndex[A] : Index[IList[A],  Int, A]    = filterIndexIndex

  def atIndex[S, I, A](implicit ev: At[S, I, A]) = new Index[S, I, A] {
    def index(i: I) = ev.at(i) |->> monocle.std.option.some
  }

  def filterIndexIndex[S, I, A](implicit filter: FilterIndex[S, I, A], ev: Equal[I]) = new Index[S, I, A] {
    def index(i: I) = filter.filterIndex(_ === i)
  }

}


