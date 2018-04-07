package monocle.function

import monocle.function.fields._
import monocle.{Iso, Optional, Prism}

import scala.annotation.implicitNotFound
import scalaz.{ICons, INil}

/**
 * Typeclass that defines a [[Prism]] between an `S` and its head `A` and tail `S`
 * @tparam S source of [[Prism]] and tail of [[Prism]] target
 * @tparam A head of [[Prism]] target, `A` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Cons[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Cons[S, A] extends Serializable {
  def cons: Prism[S, (A, S)]

  def headOption: Optional[S, A] = cons composeLens first
  def tailOption: Optional[S, S] = cons composeLens second
}

trait ConsFunctions {
  final def cons[S, A](implicit ev: Cons[S, A]): Prism[S, (A, S)] = ev.cons

  final def headOption[S, A](implicit ev: Cons[S, A]): Optional[S, A] = ev.headOption
  final def tailOption[S, A](implicit ev: Cons[S, A]): Optional[S, S] = ev.tailOption

  /** append an element to the head */
  final def _cons[S, A](head: A, tail: S)(implicit ev: Cons[S, A]): S =
  ev.cons.reverseGet((head, tail))

  /** deconstruct an S between its head and tail */
  final def _uncons[S, A](s: S)(implicit ev: Cons[S, A]): Option[(A, S)] =
  ev.cons.getOption(s)
}

object Cons extends ConsFunctions {

  def apply[S, A, B](prism: Prism[S, (B, S)]) : Cons[S, B] = new Cons[S, B] {
    val cons: Prism[S, (B, S)] = prism
  }

  /** lift an instance of [[Cons]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Cons[A, B]): Cons[S, B] = Cons(
    iso composePrism ev.cons composeIso iso.reverse.second
  )

  /************************************************************************************************/
  /** Std instances                                                                               */
  /************************************************************************************************/
  import scala.collection.immutable.Stream.#::

  implicit def listCons[A]: Cons[List[A], A] = Cons(
    Prism[List[A], (A, List[A])]{
      case Nil     => None
      case x :: xs => Some((x, xs))
    }{ case (a, s) => a :: s }
  )

  implicit def streamCons[A]: Cons[Stream[A], A] = Cons(
    Prism[Stream[A], (A, Stream[A])]{
      case scala.collection.immutable.Stream.Empty => None
      case x #:: xs => Some((x, xs))
    }{ case (a, s) => a #:: s }
  )

  implicit val stringCons: Cons[String, Char] = Cons(
    Prism[String, (Char, String)](s =>
      if(s.isEmpty) None else Some((s.head, s.tail))
    ){ case (h, t) => h + t }
  )

  implicit def vectorCons[A]: Cons[Vector[A], A] = Cons(
    Prism[Vector[A], (A, Vector[A])]{
      case Vector() => None
      case x +: xs  => Some((x, xs))
    }{ case (a, s) => a +: s }
  )

  /************************************************************************************************/
  /** Scalaz instances                                                                            */
  /************************************************************************************************/

  import scalaz.IList

  implicit def iListCons[A]: Cons[IList[A], A] = Cons(
    Prism[IList[A], (A, IList[A])]{
      case INil()       => None
      case ICons(x, xs) => Some((x, xs))
    }{ case (a, s) => ICons(a, s) }
  )
}