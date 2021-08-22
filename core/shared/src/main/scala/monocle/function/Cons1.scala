package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/** Typeclass that defines an [[Iso]] between an `S` and its head `H` and tail `T` [[Cons1]] is like [[Cons]] but for
  * types that have *always* an head and tail, e.g. a non empty list
  * @tparam S
  *   source of [[Iso]]
  * @tparam H
  *   head of [[Iso]] target, `A` is supposed to be unique for a given `S`
  * @tparam T
  *   tail of [[Iso]] target, `T` is supposed to be unique for a given `S`
  */
@implicitNotFound(
  "Could not find an instance of Cons1[${S}, ${H}, ${T}], please check Monocle instance location policy to " + "find out which import is necessary"
)
@deprecated("no replacement", since = "3.0.0-M1")
abstract class Cons1[S, H, T] extends Serializable {
  def cons1: Iso[S, (H, T)]

  def head: Lens[S, H] = cons1.composeLens(Field1.first)
  def tail: Lens[S, T] = cons1.composeLens(Field2.second)
}

trait Cons1Functions {
  @deprecated("no replacement", since = "3.0.0-M1")
  final def cons1[S, H, T](implicit ev: Cons1[S, H, T]): Iso[S, (H, T)] = ev.cons1

  @deprecated("no replacement", since = "3.0.0-M1")
  final def head[S, H, T](implicit ev: Cons1[S, H, T]): Lens[S, H] = ev.head

  @deprecated("no replacement", since = "3.0.0-M1")
  final def tail[S, H, T](implicit ev: Cons1[S, H, T]): Lens[S, T] = ev.tail

  /** append an element to the head */
  @deprecated("no replacement", since = "3.0.0-M1")
  final def _cons1[S, H, T](head: H, tail: T)(implicit ev: Cons1[S, H, T]): S =
    ev.cons1.reverseGet((head, tail))

  /** deconstruct an S between its head and tail */
  @deprecated("no replacement", since = "3.0.0-M1")
  final def _uncons1[S, H, T](s: S)(implicit ev: Cons1[S, H, T]): (H, T) =
    ev.cons1.get(s)
}

object Cons1 extends Cons1Functions {
  def apply[S, H, T](iso: Iso[S, (H, T)]): Cons1[S, H, T] =
    new Cons1[S, H, T] {
      val cons1: Iso[S, (H, T)] = iso
    }

  /** lift an instance of [[Cons1]] using an [[Iso]] */
  def fromIso[S, A, H, T](iso: Iso[S, A])(implicit ev: Cons1[A, H, T]): Cons1[S, H, T] =
    Cons1(
      iso.andThen(ev.cons1)
    )

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */
  implicit def tuple2Cons1[A1, A2]: Cons1[(A1, A2), A1, A2] =
    Cons1(
      Iso[(A1, A2), (A1, A2)](identity)(identity)
    )

  implicit def tuple3Cons1[A1, A2, A3]: Cons1[(A1, A2, A3), A1, (A2, A3)] =
    Cons1(
      Iso[(A1, A2, A3), (A1, (A2, A3))](t => (t._1, (t._2, t._3))) { case (h, t) => (h, t._1, t._2) }
    )

  implicit def tuple4Cons1[A1, A2, A3, A4]: Cons1[(A1, A2, A3, A4), A1, (A2, A3, A4)] =
    Cons1(
      Iso[(A1, A2, A3, A4), (A1, (A2, A3, A4))](t => (t._1, (t._2, t._3, t._4))) { case (h, t) =>
        (h, t._1, t._2, t._3)
      }
    )

  implicit def tuple5Cons1[A1, A2, A3, A4, A5]: Cons1[(A1, A2, A3, A4, A5), A1, (A2, A3, A4, A5)] =
    Cons1(
      Iso[(A1, A2, A3, A4, A5), (A1, (A2, A3, A4, A5))](t => (t._1, (t._2, t._3, t._4, t._5))) { case (h, t) =>
        (h, t._1, t._2, t._3, t._4)
      }
    )

  implicit def tuple6Cons1[A1, A2, A3, A4, A5, A6]: Cons1[(A1, A2, A3, A4, A5, A6), A1, (A2, A3, A4, A5, A6)] =
    Cons1(
      Iso[(A1, A2, A3, A4, A5, A6), (A1, (A2, A3, A4, A5, A6))](t => (t._1, (t._2, t._3, t._4, t._5, t._6))) {
        case (h, t) => (h, t._1, t._2, t._3, t._4, t._5)
      }
    )

  /** *********************************************************************************************
    */
  /** Cats instances */
  /** *********************************************************************************************
    */
  import cats.Now
  import cats.data._
  import cats.free.Cofree

  import scala.{List => IList, Vector => IVector}

  implicit def cofreeCons1[S[_], A]: Cons1[Cofree[S, A], A, S[Cofree[S, A]]] =
    new Cons1[Cofree[S, A], A, S[Cofree[S, A]]] {
      val cons1: Iso[Cofree[S, A], (A, S[Cofree[S, A]])] =
        Iso((c: Cofree[S, A]) => (c.head, c.tail.value)) { case (h, t) => Cofree(h, Now(t)) }

      /** Overridden to prevent forcing evaluation of the `tail` when we're only interested in using the `head`
        */
      override def head: Lens[Cofree[S, A], A] =
        Lens((c: Cofree[S, A]) => c.head)(h => c => Cofree(h, c.tail))
    }

  implicit def necCons1[A]: Cons1[NonEmptyChain[A], A, Chain[A]] =
    Cons1(
      Iso((nec: NonEmptyChain[A]) => (nec.head, nec.tail)) { case (h, t) => NonEmptyChain.fromChainPrepend(h, t) }
    )

  implicit def nelCons1[A]: Cons1[NonEmptyList[A], A, IList[A]] =
    Cons1(
      Iso((nel: NonEmptyList[A]) => (nel.head, nel.tail)) { case (h, t) => NonEmptyList(h, t) }
    )

  implicit def nevCons1[A]: Cons1[NonEmptyVector[A], A, IVector[A]] =
    Cons1(
      Iso((nev: NonEmptyVector[A]) => (nev.head, nev.tail)) { case (h, t) => NonEmptyVector(h, t) }
    )

  implicit def oneAndCons1[T[_], A]: Cons1[OneAnd[T, A], A, T[A]] =
    Cons1(
      Iso[OneAnd[T, A], (A, T[A])](o => (o.head, o.tail)) { case (h, t) => OneAnd(h, t) }
    )
}
