package monocle.function

import monocle.{Iso, Optional, Prism}

import scala.annotation.implicitNotFound

/** Typeclass that defines a [[Prism]] between an `S` and its head `A` and tail `S`
  * @tparam S
  *   source of [[Prism]] and tail of [[Prism]] target
  * @tparam A
  *   head of [[Prism]] target, `A` is supposed to be unique for a given `S`
  */
@implicitNotFound(
  "Could not find an instance of Cons[${S},${A}], please check Monocle instance location policy to " + "find out which import is necessary"
)
@deprecated("no replacement", since = "3.0.0-M1")
abstract class Cons[S, A] extends Serializable {
  def cons: Prism[S, (A, S)]

  def headOption: Optional[S, A] = cons.composeLens(Field1.first)
  def tailOption: Optional[S, S] = cons.composeLens(Field2.second)
}

trait ConsFunctions {
  @deprecated("no replacement", since = "3.0.0-M1")
  final def cons[S, A](implicit ev: Cons[S, A]): Prism[S, (A, S)] = ev.cons

  @deprecated("use index(0)", since = "3.0.0-M1")
  final def headOption[S, A](implicit ev: Cons[S, A]): Optional[S, A] =
    ev.headOption

  @deprecated("no replacement", since = "3.0.0-M1")
  final def tailOption[S, A](implicit ev: Cons[S, A]): Optional[S, S] =
    ev.tailOption

  /** append an element to the head */
  @deprecated("no replacement", since = "3.0.0-M1")
  final def _cons[S, A](head: A, tail: S)(implicit ev: Cons[S, A]): S =
    ev.cons.reverseGet((head, tail))

  /** deconstruct an S between its head and tail */
  @deprecated("no replacement", since = "3.0.0-M1")
  final def _uncons[S, A](s: S)(implicit ev: Cons[S, A]): Option[(A, S)] =
    ev.cons.getOption(s)
}

object Cons extends ConsFunctions {
  def apply[S, A, B](prism: Prism[S, (B, S)]): Cons[S, B] =
    new Cons[S, B] {
      val cons: Prism[S, (B, S)] = prism
    }

  /** lift an instance of [[Cons]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Cons[A, B]): Cons[S, B] =
    Cons(
      iso.andThen(ev.cons).andThen(iso.reverse.second[B])
    )

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */
  implicit def listCons[A]: Cons[List[A], A] =
    Cons(
      Prism[List[A], (A, List[A])] {
        case Nil     => None
        case x :: xs => Some((x, xs))
      } { case (a, s) => a :: s }
    )

  implicit val stringCons: Cons[String, Char] = Cons(
    Prism[String, (Char, String)](s => if (s.isEmpty) None else Some((s.head, s.tail))) { case (h, t) =>
      s"$h$t"
    }
  )

  implicit def vectorCons[A]: Cons[Vector[A], A] =
    Cons(
      Prism[Vector[A], (A, Vector[A])] {
        case x if x.isEmpty => None
        case x +: xs        => Some((x, xs))
        case _              => None
      } { case (a, s) => a +: s }
    )

  implicit def lazyListCons[A]: Cons[LazyList[A], A] =
    Cons(
      Prism[LazyList[A], (A, LazyList[A])](xs => xs.headOption.map(_ -> xs.tail)) { case (a, s) => a #:: s }
    )

  /** *********************************************************************************************
    */
  /** Cats instances */
  /** *********************************************************************************************
    */
  import cats.data.Chain

  implicit def chainCons[A]: Cons[Chain[A], A] =
    Cons(
      Prism[Chain[A], (A, Chain[A])](_.uncons) { case (a, s) =>
        s.prepend(a)
      }
    )
}
