package monocle.function

import monocle.function.fields._
import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
  * Typeclass that defines an [[Iso]] between an `S` and its init `H` and last `T`
  * [[Snoc1]] is like [[Snoc]] but for types that have *always* an init and a last element, e.g. a non empty list
  * @tparam S source of [[Iso]]
  * @tparam I init of [[Iso]] target, `I` is supposed to be unique for a given `S`
  * @tparam L last of [[Iso]] target, `L` is supposed to be unique for a given `S`
  */
@implicitNotFound(
  "Could not find an instance of Snoc1[${S}, ${I}, ${L}], please check Monocle instance location policy to " + "find out which import is necessary"
)
abstract class Snoc1[S, I, L] extends Serializable {
  def snoc1: Iso[S, (I, L)]

  def init: Lens[S, I] = snoc1 composeLens first
  def last: Lens[S, L] = snoc1 composeLens second
}

trait Snoc1Functions {
  final def snoc1[S, I, L](implicit ev: Snoc1[S, I, L]): Iso[S, (I, L)] = ev.snoc1

  final def init[S, I, L](implicit ev: Snoc1[S, I, L]): Lens[S, I] = ev.init
  final def last[S, I, L](implicit ev: Snoc1[S, I, L]): Lens[S, L] = ev.last

  /** append an element to the end */
  final def _snoc1[S, I, L](init: I, last: L)(implicit ev: Snoc1[S, I, L]): S =
    ev.snoc1.reverseGet((init, last))

  /** deconstruct an S between its init and last */
  final def _unsnoc1[S, I, L](s: S)(implicit ev: Snoc1[S, I, L]): (I, L) =
    ev.snoc1.get(s)
}

object Snoc1 extends Snoc1Functions {
  def apply[S, I, L](iso: Iso[S, (I, L)]): Snoc1[S, I, L] =
    new Snoc1[S, I, L] {
      override val snoc1: Iso[S, (I, L)] = iso
    }

  /** lift an instance of [[Snoc1]] using an [[Iso]] */
  def fromIso[S, A, I, L](iso: Iso[S, A])(implicit ev: Snoc1[A, I, L]): Snoc1[S, I, L] =
    Snoc1(
      iso composeIso ev.snoc1
    )

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */
  implicit def tuple2Snoc1[A1, A2]: Snoc1[(A1, A2), A1, A2] =
    Snoc1(
      Iso[(A1, A2), (A1, A2)](identity)(identity)
    )

  implicit def tuple3Snoc1[A1, A2, A3]: Snoc1[(A1, A2, A3), (A1, A2), A3] =
    Snoc1(
      Iso[(A1, A2, A3), ((A1, A2), A3)](t => ((t._1, t._2), t._3)) { case (i, l) => (i._1, i._2, l) }
    )

  implicit def tuple4Snoc1[A1, A2, A3, A4]: Snoc1[(A1, A2, A3, A4), (A1, A2, A3), A4] =
    Snoc1(
      Iso[(A1, A2, A3, A4), ((A1, A2, A3), A4)](t => ((t._1, t._2, t._3), t._4)) { case (i, l) =>
        (i._1, i._2, i._3, l)
      }
    )

  implicit def tuple5Snoc1[A1, A2, A3, A4, A5]: Snoc1[(A1, A2, A3, A4, A5), (A1, A2, A3, A4), A5] =
    Snoc1(
      Iso[(A1, A2, A3, A4, A5), ((A1, A2, A3, A4), A5)](t => ((t._1, t._2, t._3, t._4), t._5)) { case (i, l) =>
        (i._1, i._2, i._3, i._4, l)
      }
    )

  implicit def tuple6Snoc1[A1, A2, A3, A4, A5, A6]: Snoc1[(A1, A2, A3, A4, A5, A6), (A1, A2, A3, A4, A5), A6] =
    Snoc1(
      Iso[(A1, A2, A3, A4, A5, A6), ((A1, A2, A3, A4, A5), A6)](t => ((t._1, t._2, t._3, t._4, t._5), t._6)) {
        case (i, l) => (i._1, i._2, i._3, i._4, i._5, l)
      }
    )

  /** *********************************************************************************************
    */
  /** Cats instances */
  /** *********************************************************************************************
    */
  import cats.data.{Chain, NonEmptyChain, NonEmptyList, NonEmptyVector}
  import scala.{List => IList, Vector => IVector}

  implicit def necSnoc1[A]: Snoc1[NonEmptyChain[A], Chain[A], A] =
    new Snoc1[NonEmptyChain[A], Chain[A], A] {
      val snoc1: Iso[NonEmptyChain[A], (Chain[A], A)] =
        Iso { nec: NonEmptyChain[A] =>
          Snoc.chainSnoc.snoc.getOption(nec.toChain) match {
            case Some(tuple) => tuple
            case None        => (nec.tail, nec.head)
          }
        } { case (c, a) =>
          NonEmptyChain.fromChainAppend(c, a)
        }
    }

  implicit def nelSnoc1[A]: Snoc1[NonEmptyList[A], IList[A], A] =
    new Snoc1[NonEmptyList[A], IList[A], A] {
      val snoc1: Iso[NonEmptyList[A], (IList[A], A)] =
        Iso((nel: NonEmptyList[A]) => nel.init -> nel.last) { case (i, l) => NonEmptyList(l, i.reverse).reverse }
    }

  implicit def nevSnoc1[A]: Snoc1[NonEmptyVector[A], IVector[A], A] =
    new Snoc1[NonEmptyVector[A], IVector[A], A] {
      val snoc1: Iso[NonEmptyVector[A], (IVector[A], A)] =
        Iso((nev: NonEmptyVector[A]) => nev.init -> nev.last) { case (i, l) => NonEmptyVector(l, i.reverse).reverse }
    }
}
