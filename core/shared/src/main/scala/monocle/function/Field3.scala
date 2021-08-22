package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/** Typeclass that defines a [[Lens]] from an `S` to its third element of type `A`
  * @tparam S
  *   source of [[Lens]]
  * @tparam A
  *   target of [[Lens]], `A` is supposed to be unique for a given `S`
  */
@implicitNotFound(
  "Could not find an instance of Field3[${S},${A}], please check Monocle instance location policy to " + "find out which import is necessary"
)
@deprecated("use Focus[$TupleType](_._3)", since = "3.0.0-M2")
abstract class Field3[S, A] extends Serializable {
  def third: Lens[S, A]
}

trait Field3Functions {
  @deprecated("use Focus[$TupleType](_._3)", since = "3.0.0-M2")
  def third[S, A](implicit ev: Field3[S, A]): Lens[S, A] = ev.third
}

object Field3 extends Field3Functions {
  def apply[S, A](lens: Lens[S, A]): Field3[S, A] =
    new Field3[S, A] {
      override val third: Lens[S, A] = lens
    }

  /** lift an instance of [[Field3]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field3[A, B]): Field3[S, B] =
    Field3(iso.andThen(ev.third))

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */
  implicit def tuple3Field3[A1, A2, A3]: Field3[(A1, A2, A3), A3] =
    Field3(
      Lens((_: (A1, A2, A3))._3)(a => t => t.copy(_3 = a))
    )

  implicit def tuple4Field3[A1, A2, A3, A4]: Field3[(A1, A2, A3, A4), A3] =
    Field3(
      Lens((_: (A1, A2, A3, A4))._3)(a => t => t.copy(_3 = a))
    )

  implicit def tuple5Field3[A1, A2, A3, A4, A5]: Field3[(A1, A2, A3, A4, A5), A3] =
    Field3(
      Lens((_: (A1, A2, A3, A4, A5))._3)(a => t => t.copy(_3 = a))
    )

  implicit def tuple6Field3[A1, A2, A3, A4, A5, A6]: Field3[(A1, A2, A3, A4, A5, A6), A3] =
    Field3(
      Lens((_: (A1, A2, A3, A4, A5, A6))._3)(a => t => t.copy(_3 = a))
    )
}
