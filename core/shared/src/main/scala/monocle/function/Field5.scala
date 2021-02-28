package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/** Typeclass that defines a [[Lens]] from an `S` to its fifth element of type `A`
  * @tparam S source of [[Lens]]
  * @tparam A target of [[Lens]], `A` is supposed to be unique for a given `S`
  */
@implicitNotFound(
  "Could not find an instance of Field5[${S},${A}], please check Monocle instance location policy to " + "find out which import is necessary"
)
@deprecated("use Focus[$TupleType](_._5)", since = "3.0.0-M2")
abstract class Field5[S, A] extends Serializable {
  def fifth: Lens[S, A]
}

trait Field5Functions {
  @deprecated("use Focus[$TupleType](_._5)", since = "3.0.0-M2")
  def fifth[S, A](implicit ev: Field5[S, A]): Lens[S, A] = ev.fifth
}

object Field5 extends Field5Functions {
  def apply[S, A](lens: Lens[S, A]): Field5[S, A] =
    new Field5[S, A] {
      override val fifth: Lens[S, A] = lens
    }

  /** lift an instance of [[Field5]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field5[A, B]): Field5[S, B] =
    Field5(iso.andThen(ev.fifth))

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */
  implicit def tuple5Field5[A1, A2, A3, A4, A5]: Field5[(A1, A2, A3, A4, A5), A5] =
    Field5(
      Lens((_: (A1, A2, A3, A4, A5))._5)(a => t => t.copy(_5 = a))
    )

  implicit def tuple6Field5[A1, A2, A3, A4, A5, A6]: Field5[(A1, A2, A3, A4, A5, A6), A5] =
    Field5(
      Lens((_: (A1, A2, A3, A4, A5, A6))._5)(a => t => t.copy(_5 = a))
    )
}
