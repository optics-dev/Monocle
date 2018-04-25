package monocle.function

import monocle.function.fields._
import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines an [[Iso]] between an `S` and its head `H` and tail `T`
 * [[Cons1]] is like [[Cons]] but for types that have *always* an head and tail, e.g. a non empty list
 * @tparam S source of [[Iso]]
 * @tparam H head of [[Iso]] target, `A` is supposed to be unique for a given `S`
 * @tparam T tail of [[Iso]] target, `T` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Cons1[${S}, ${H}, ${T}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Cons1[S, H, T] extends Serializable {
  def cons1: Iso[S, (H, T)]

  def head: Lens[S, H] = cons1 composeLens first
  def tail: Lens[S, T] = cons1 composeLens second
}

trait Cons1Functions {
  final def cons1[S, H, T](implicit ev: Cons1[S, H, T]): Iso[S, (H, T)] = ev.cons1

  final def head[S, H, T](implicit ev: Cons1[S, H, T]): Lens[S, H] = ev.head
  final def tail[S, H, T](implicit ev: Cons1[S, H, T]): Lens[S, T] = ev.tail

  /** append an element to the head */
  final def _cons1[S, H, T](head: H, tail: T)(implicit ev: Cons1[S, H, T]): S =
  ev.cons1.reverseGet((head, tail))

  /** deconstruct an S between its head and tail */
  final def _uncons1[S, H, T](s: S)(implicit ev: Cons1[S, H, T]): (H, T) =
  ev.cons1.get(s)
}

object Cons1 extends Cons1Functions {
  /** lift an instance of [[Cons1]] using an [[Iso]] */
  def fromIso[S, A, H, T](iso: Iso[S, A])(implicit ev: Cons1[A, H, T]): Cons1[S, H, T] = new Cons1[S, H, T] {
    val cons1: Iso[S, (H, T)] =
      iso composeIso ev.cons1
  }

  /************************************************************************************************/
  /** Std instances                                                                               */
  /************************************************************************************************/

  implicit def tuple2Cons1[A1, A2]: Cons1[(A1, A2), A1, A2] = new Cons1[(A1, A2), A1, A2] {
    val cons1 = Iso[(A1, A2), (A1, A2)](identity)(identity)
  }

  implicit def tuple3Cons1[A1, A2, A3]: Cons1[(A1, A2, A3), A1, (A2, A3)] = new Cons1[(A1, A2, A3), A1, (A2, A3)] {
    val cons1 = Iso[(A1, A2, A3), (A1, (A2, A3))](t => (t._1, (t._2, t._3))){ case (h, t) => (h, t._1, t._2) }
  }

  implicit def tuple4Cons1[A1, A2, A3, A4]: Cons1[(A1, A2, A3, A4), A1, (A2, A3, A4)] = new Cons1[(A1, A2, A3, A4), A1, (A2, A3, A4)]{
    val cons1 = Iso[(A1, A2, A3, A4), (A1, (A2, A3, A4))](t => (t._1, (t._2, t._3, t._4))){ case (h, t) => (h, t._1, t._2, t._3) }
  }

  implicit def tuple5Cons1[A1, A2, A3, A4, A5]: Cons1[(A1, A2, A3, A4, A5), A1, (A2, A3, A4, A5)] = new Cons1[(A1, A2, A3, A4, A5), A1, (A2, A3, A4, A5)]{
    val cons1 = Iso[(A1, A2, A3, A4, A5), (A1, (A2, A3, A4, A5))](t => (t._1, (t._2, t._3, t._4, t._5))){ case (h, t) => (h, t._1, t._2, t._3, t._4) }
  }

  implicit def tuple5Snoc1[A1, A2, A3, A4, A5]: Snoc1[(A1, A2, A3, A4, A5), (A1, A2, A3, A4), A5] = new Snoc1[(A1, A2, A3, A4, A5), (A1, A2, A3, A4), A5]{
    def snoc1 = Iso[(A1, A2, A3, A4, A5), ((A1, A2, A3, A4), A5)](t => ((t._1, t._2, t._3, t._4), t._5)){ case (i, l) => (i._1, i._2, i._3, i._4, l) }
  }

  implicit def tuple6Cons1[A1, A2, A3, A4, A5, A6]: Cons1[(A1, A2, A3, A4, A5, A6), A1, (A2, A3, A4, A5, A6)] = new Cons1[(A1, A2, A3, A4, A5, A6), A1, (A2, A3, A4, A5, A6)]{
    val cons1 = Iso[(A1, A2, A3, A4, A5, A6), (A1, (A2, A3, A4, A5, A6))](t => (t._1, (t._2, t._3, t._4, t._5, t._6))){ case (h, t) => (h, t._1, t._2, t._3, t._4, t._5) }
  }

  /************************************************************************************************/
  /** Cats instances                                                                            */
  /************************************************************************************************/
  import cats.Now
  import cats.data.{NonEmptyList, NonEmptyVector, OneAnd}
  import cats.free.Cofree
  import scala.{List => IList, Vector => IVector}

  implicit def cofreeCons1[S[_], A]: Cons1[Cofree[S, A], A, S[Cofree[S, A]]] =
    new Cons1[Cofree[S, A], A, S[Cofree[S, A]]] {

      val cons1: Iso[Cofree[S, A], (A, S[Cofree[S, A]])]  =
        Iso((c: Cofree[S, A]) => (c.head, c.tail.value)){ case (h, t) => Cofree(h, Now(t)) }

      /** Overridden to prevent forcing evaluation of the `tail` when we're only
        * interested in using the `head` */
      override def head: Lens[Cofree[S, A], A] =
      Lens((c: Cofree[S, A]) => c.head)(h => c => Cofree(h, c.tail))
    }

  implicit def nelCons1[A]: Cons1[NonEmptyList[A], A, IList[A]] =
    new Cons1[NonEmptyList[A],A,IList[A]]{
      val cons1: Iso[NonEmptyList[A], (A, IList[A])] =
        Iso((nel: NonEmptyList[A]) => (nel.head,nel.tail)){case (h,t) => NonEmptyList(h, t)}
    }

  implicit def nevCons1[A]: Cons1[NonEmptyVector[A], A, IVector[A]] =
    new Cons1[NonEmptyVector[A],A,IVector[A]]{
      val cons1: Iso[NonEmptyVector[A], (A, IVector[A])] =
        Iso((nev: NonEmptyVector[A]) => (nev.head,nev.tail)){case (h,t) => NonEmptyVector(h, t)}
    }

  implicit def oneAndCons1[T[_], A]: Cons1[OneAnd[T, A], A, T[A]] = new Cons1[OneAnd[T, A], A, T[A]] {
    val cons1 = Iso[OneAnd[T, A], (A, T[A])](o => (o.head, o.tail)){ case (h, t) => OneAnd(h, t)}
  }
}
