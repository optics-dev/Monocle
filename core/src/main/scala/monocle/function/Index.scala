package monocle.function

import monocle.{Traversal, SimpleTraversal}
import scalaz.Applicative

trait Index[S, I, A] {

  /** Creates a Traversal from S to 0 or 1 A */
  def index(i: I): SimpleTraversal[S, A]

  /** Creates a Traversal from S to all A with an index matching the predicate */
  def filterIndexes(predicate: I => Boolean): SimpleTraversal[S, A]

}

object Index {

  def index[S, I, A](i: I)(implicit ev: Index[S, I, A]): SimpleTraversal[S, A] = ev.index(i)

  def filterIndexes[S, I, A](predicate: I => Boolean)
                            (implicit ev: Index[S, I, A]): SimpleTraversal[S, A] = ev.filterIndexes(predicate)

  implicit def indexWitAt[S, I, A](implicit at: At[S, I, A]) = new Index[S, I, A] {

    import At._
    import monocle.std.option.some
    import monocle.syntax.traversal._

    def index(i: I) = at(i) |->> some

    def filterIndexes(predicate: I => Boolean) = filterAt(predicate) |->> some
  }


  implicit def indexWithSplit[S, I, A](implicit ev: Split[S, I, A]): Index[S, I, A] = new Index[S, I, A]{
    def index(i: I): SimpleTraversal[S, A] = new Traversal[S, S, A, A] {
      def multiLift[F[_] : Applicative](from: S, f: (A) => F[A]): F[S] =
        ev.split(from, i).map{ case (before, a, after) =>
          Applicative[F].map(f(a))(newA => ev.merge(before, newA, after))
        }.getOrElse(Applicative[F].point(from))
    }

    def filterIndexes(predicate: I => Boolean): SimpleTraversal[S, A] = new Traversal[S, S, A, A] {
      def multiLift[F[_] : Applicative](from: S, f: A => F[A]): F[S] =
        Applicative[F].map(filterLiftList(ev.toIndexedList(from), f)(predicate))(ev.fromIndexedList)
    }
  }

  /** Split is type class to facilitate creation of Index*/
  trait Split[S, I, A]{
    /**
     * Split an S at index
     * @return if S has no value at index then None, otherwise returns all values before index,
     *         the value at the index and all values after index
     */
    def split(from: S, index: I): Option[(S, A, S)]

    def merge(before: S, at: A, after: S): S

    def toIndexedList(from: S): List[(I, A)]

    def fromIndexedList(list: List[(I, A)]): S

  }

  implicit def listSplit[A] = new Split[List[A], Int, A] {
    def merge(before: List[A], at: A, after: List[A]): List[A] = before ::: at :: after

    def split(from: List[A], index: Int): Option[(List[A], A, List[A])] =
      from.splitAt(index) match {
        case (before, at :: after) => Some(before, at, after)
        case _                     => None
      }

    def fromIndexedList(list: List[(Int, A)]): List[A] = list.map(_._2)

    def toIndexedList(from: List[A]): List[(Int, A)] = from.zipWithIndex.map(_.swap)
  }

  implicit val stringSplit = new Split[String, Int, Char] {
    def merge(before: String, at: Char, after: String): String = before + at + after

    def split(from: String, index: Int): Option[(String, Char, String)] = {
      val (before, atAndAfter) = from.splitAt(index)
      val (at, after)          = atAndAfter.splitAt(1)
      at.headOption.map((before, _, after))
    }

    def fromIndexedList(list: List[(Int, Char)]): String = list.map(_._2).mkString("")

    def toIndexedList(from: String): List[(Int, Char)] = from.toList.zipWithIndex.map(_.swap)
  }

  private def filterLiftList[I, A, F[_]](list: List[(I, A)], f: A => F[A])(predicate: I => Boolean)
                                        (implicit ev: Applicative[F]): F[List[(I, A)]] =
    list.map{ case (i, a) => ev.map(if(predicate(i)) f(a) else ev.point(a))(i -> _) }
      .foldRight(ev.point(List.empty[(I, A)]))( ev.apply2(_, _)(_ :: _) )


}


