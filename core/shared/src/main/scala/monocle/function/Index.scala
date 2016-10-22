package monocle.function

import monocle.{Iso, Optional}

import scala.annotation.implicitNotFound
import scalaz.Id._

/**
 * Typeclass that defines an [[Optional]] from an `S` to an `A` at an index `I`
 * [[Index]] is less powerful than [[At]] as it cannot create or delete value
 * @tparam S source of [[Optional]]
 * @tparam I index
 * @tparam A target of [[Optional]], `A` is supposed to be unique for a given pair `(S, I)`
 */
@implicitNotFound("Could not find an instance of Index[${S},${I},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Index[S, I, A] extends Serializable {
  def index(i: I): Optional[S, A]
}

trait IndexFunctions {
  def index[S, I, A](i: I)(implicit ev: Index[S, I, A]): Optional[S, A] = ev.index(i)

  def atIndex[S, I, A](implicit ev: At[S, I, Option[A]]) = new Index[S, I, A] {
    def index(i: I) = ev.at(i) composePrism monocle.std.option.some
  }
}

object Index extends IndexFunctions{
  /** lift an instance of [[Index]] using an [[Iso]] */
  def fromIso[S, A, I, B](iso: Iso[S, A])(implicit ev: Index[A, I, B]): Index[S, I, B] = new Index[S, I, B] {
    def index(i: I): Optional[S, B] =
      iso composeOptional ev.index(i)
  }

  /************************************************************************************************/
  /** Std instances                                                                               */
  /************************************************************************************************/
  import scalaz.syntax.traverse._
  import scalaz.std.list._
  import scalaz.std.stream._

  implicit def listIndex[A]: Index[List[A], Int, A] = new Index[List[A], Int, A] {
    def index(i: Int) = Optional[List[A], A](
      l      => if(i < 0) None else l.drop(i).headOption)(
      a => l => l.zipWithIndex.traverse[Id, A]{
        case (_    , index) if index == i => a
        case (value, index)               => value
      }
    )
  }

  implicit def mapIndex[K, V]: Index[Map[K, V], K, V] = atIndex

  implicit def streamIndex[A]: Index[Stream[A], Int, A] = new Index[Stream[A], Int, A] {
    def index(i: Int) = Optional[Stream[A], A](
      s      => if(i < 0) None else s.drop(i).headOption)(
      a => s => s.zipWithIndex.traverse[Id, A]{
        case (_    , index) if index == i => a
        case (value, index)               => value
      }
    )
  }

  implicit val stringIndex: Index[String, Int, Char] = new Index[String, Int, Char]{
    def index(i: Int) = monocle.std.string.stringToList composeOptional Index.index[List[Char], Int, Char](i)
  }

  implicit def vectorIndex[A]: Index[Vector[A], Int, A] = new Index[Vector[A], Int, A] {
    def index(i: Int) =
      Optional[Vector[A], A](v =>
        if(v.isDefinedAt(i)) Some(v(i))     else None)(a => v =>
        if(v.isDefinedAt(i)) v.updated(i,a) else v)
  }

  /************************************************************************************************/
  /** Scalaz instances                                                                            */
  /************************************************************************************************/

  import monocle.function.Cons1.{oneAndCons1, nelCons1}
  import scalaz.{==>>, IList, Order, NonEmptyList, OneAnd}

  implicit def iListIndex[A]: Index[IList[A], Int, A] = new Index[IList[A], Int, A] {
    def index(i: Int) = Optional[IList[A], A](
      il      => if(i < 0) None else il.drop(i).headOption)(
      a => il => il.zipWithIndex.traverse[Id, A]{
        case (_    , index) if index == i => a
        case (value, index)               => value
      }
    )
  }

  implicit def iMapIndex[K: Order, V]: Index[K ==>> V, K, V] = atIndex

  implicit def nelIndex[A]: Index[NonEmptyList[A], Int, A] =
    new Index[NonEmptyList[A], Int, A] {
      def index(i: Int): Optional[NonEmptyList[A], A] = i match {
        case 0 => nelCons1.head.asOptional
        case _ => nelCons1.tail composeOptional iListIndex.index(i-1)
      }
    }

  implicit def oneAndIndex[T[_], A](implicit ev: Index[T[A], Int, A]): Index[OneAnd[T, A], Int, A] =
    new Index[OneAnd[T, A], Int, A]{
      def index(i: Int) = i match {
        case 0 => oneAndCons1[T, A].head.asOptional
        case _ => oneAndCons1[T, A].tail composeOptional ev.index(i - 1)
      }
    }
}