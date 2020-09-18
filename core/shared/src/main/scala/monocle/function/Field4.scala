package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
  * Typeclass that defines a [[Lens]] from an `S` to its fourth element of type `A`
  * @tparam S source of [[Lens]]
  * @tparam A target of [[Lens]], `A` is supposed to be unique for a given `S`
  */
@implicitNotFound(
  "Could not find an instance of Field4[${S},${A}], please check Monocle instance location policy to " + "find out which import is necessary"
)
abstract class Field4[S, A] extends Serializable {
  def fourth: Lens[S, A]
}

trait Field4Functions {
  def fourth[S, A](implicit ev: Field4[S, A]): Lens[S, A] = ev.fourth
}

object Field4 extends Field4Functions {
  def apply[S, A](lens: Lens[S, A]): Field4[S, A] =
    new Field4[S, A] {
      override val fourth: Lens[S, A] = lens
    }

  /** lift an instance of [[Field4]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field4[A, B]): Field4[S, B] =
    Field4(
      iso composeLens ev.fourth
    )

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */
  implicit def tuple4Field4[A1, A2, A3, A4]: Field4[(A1, A2, A3, A4), A4] =
    Field4(
      Lens((_: (A1, A2, A3, A4))._4)(a => t => t.copy(_4 = a))
    )

  implicit def tuple5Field4[A1, A2, A3, A4, A5]: Field4[(A1, A2, A3, A4, A5), A4] =
    Field4(
      Lens((_: (A1, A2, A3, A4, A5))._4)(a => t => t.copy(_4 = a))
    )

  implicit def tuple6Field4[A1, A2, A3, A4, A5, A6]: Field4[(A1, A2, A3, A4, A5, A6), A4] =
    Field4(
      Lens((_: (A1, A2, A3, A4, A5, A6))._4)(a => t => t.copy(_4 = a))
    )
}
