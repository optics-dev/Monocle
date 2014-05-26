package monocle.function

import monocle.{Optional, SimpleOptional}
import scalaz.IList._
import scalaz.std.list._
import scalaz.std.stream._
import scalaz.std.vector._
import scalaz.syntax.traverse._
import scalaz.{Traverse, Applicative, IList}

trait Index[S, I, A] {

  /**
   * Creates a Traversal from S to 0 or 1 A
   * index is less powerful than at as we can only modify an index that already exist
   */
  def index(i: I): SimpleOptional[S, A]

}

object Index extends IndexInstances

trait IndexInstances {

  def index[S, I, A](i: I)(implicit ev: Index[S, I, A]): SimpleOptional[S, A] = ev.index(i)


  implicit def mapIndex[K, V]: Index[Map[K, V], K  , V] = atIndex

  implicit def listIndex[A]   = traverseIndex[List, A](_.zipWithIndex)
  implicit def streamIndex[A] = traverseIndex[Stream, A](_.zipWithIndex)
  implicit def vectorIndex[A] = traverseIndex[Vector, A](_.zipWithIndex)
  implicit def iListIndex[A]  = traverseIndex[IList, A](_.zipWithIndex)

  implicit val stringIndex  = new Index[String, Int, Char]{
    def index(i: Int) =
      monocle.std.string.stringToList composeOptional listIndex.index(i)
  }


  def atIndex[S, I, A](implicit ev: At[S, I, A]) = new Index[S, I, A] {
    def index(i: I) = ev.at(i) composeOptional monocle.std.option.some
  }

  def traverseIndex[S[_]: Traverse, A](zipWithIndex: S[A] => S[(A, Int)]): Index[S[A], Int, A] = new Index[S[A], Int, A]{
    def index(i: Int) = new Optional[S[A], S[A], A, A] {
      def multiLift[F[_] : Applicative](from: S[A], f: A => F[A]): F[S[A]] =
        zipWithIndex(from).traverse { case (a, j) =>
          if(j == i) f(a) else Applicative[F].point(a)
        }
    }
  }

}


