package monocle.function

import monocle.{Iso, Optional}

import scala.annotation.implicitNotFound
import scala.util.Try
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

  @deprecated("use Index.fromAt", since = "1.4.0")
  def atIndex[S, I, A](implicit ev: At[S, I, Option[A]]) = Index.fromAt[S, I, A]
}

object Index extends IndexFunctions{

  def apply[S, I, A](optional : I => Optional[S, A]): Index[S, I, A] = new Index[S, I, A] {
    override def index(i: I): Optional[S, A] = optional(i)
  }

  /** lift an instance of [[Index]] using an [[Iso]] */
  def fromIso[S, A, I, B](iso: Iso[S, A])(implicit ev: Index[A, I, B]): Index[S, I, B] = Index(
    iso composeOptional ev.index(_)
  )

  def fromAt[S, I, A](implicit ev: At[S, I, Option[A]]): Index[S, I, A] = Index(
    ev.at(_) composePrism monocle.std.option.some
  )

  /************************************************************************************************/
  /** Std instances                                                                               */
  /************************************************************************************************/
  import scalaz.syntax.traverse._

  implicit def listIndex[A]: Index[List[A], Int, A] = Index(i =>
    if (i < 0)
      Optional[List[A], A](_ => None)(_ => identity)
    else
      Optional[List[A], A](_.drop(i).headOption)(a => s => Try(s.updated(i, a)).getOrElse(s))
  )

  implicit def mapIndex[K, V]: Index[Map[K, V], K, V] = fromAt

  implicit def streamIndex[A]: Index[Stream[A], Int, A] = Index(i =>
    if (i < 0)
      Optional[Stream[A], A](_ => None)(_ => identity)
    else
      Optional[Stream[A], A](_.drop(i).headOption)(a => s => Try(s.updated(i, a)).getOrElse(s))
  )

  implicit val stringIndex: Index[String, Int, Char] = Index(
    monocle.std.string.stringToList composeOptional Index.index[List[Char], Int, Char](_)
  )

  implicit def vectorIndex[A]: Index[Vector[A], Int, A] = Index(i => Optional[Vector[A], A]
    (v => if(v.isDefinedAt(i)) Some(v(i)) else None)
    (a => v => if(v.isDefinedAt(i)) v.updated(i,a) else v)
  )

  /************************************************************************************************/
  /** Scalaz instances                                                                            */
  /************************************************************************************************/
  import monocle.function.Cons1.{oneAndCons1, nelCons1}
  import scalaz.{==>>, IList, Order, NonEmptyList, OneAnd}

  implicit def iListIndex[A]: Index[IList[A], Int, A] = Index(i => Optional[IList[A], A]
    (if(i < 0) _ => None else _.drop(i).headOption)
    (a => il => il.zipWithIndex.traverse[Id, A]{
      case (_    , index) if index == i => a
      case (value, index)               => value
    })
  )

  implicit def iMapIndex[K: Order, V]: Index[K ==>> V, K, V] = fromAt

  implicit def nelIndex[A]: Index[NonEmptyList[A], Int, A] = Index {
    case 0 => nelCons1.head.asOptional
    case i => nelCons1.tail composeOptional iListIndex.index(i - 1)
  }


  implicit def oneAndIndex[T[_], A](implicit ev: Index[T[A], Int, A]): Index[OneAnd[T, A], Int, A] = Index {
    case 0 => oneAndCons1[T, A].head.asOptional
    case i => oneAndCons1[T, A].tail composeOptional ev.index(i - 1)
  }
}
