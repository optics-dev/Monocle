package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/** Typeclass that defines a [[Lens]] from an `S` to its first element of type `A`
  * @tparam S source of [[Lens]]
  * @tparam A target of [[Lens]], `A` is supposed to be unique for a given `S`
  */
@implicitNotFound(
  "Could not find an instance of Field1[${S},${A}], please check Monocle instance location policy to " + "find out which import is necessary"
)
abstract class Field1[S, A] extends Serializable {
  def first: Lens[S, A]
}

trait Field1Functions {
  def first[S, A](implicit ev: Field1[S, A]): Lens[S, A] = ev.first
}

object Field1 extends Field1Functions {
  def apply[S, A](lens: Lens[S, A]): Field1[S, A] =
    new Field1[S, A] {
      override val first: Lens[S, A] = lens
    }

  /** lift an instance of [[Field1]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field1[A, B]): Field1[S, B] =
    Field1(
      iso composeLens ev.first
    )

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */
  implicit def tuple1Field1[A]: Field1[Tuple1[A], A] =
    Field1(
      Lens((_: Tuple1[A])._1)(a => _ => Tuple1(a))
    )

  implicit def tuple2Field1[A1, A2]: Field1[(A1, A2), A1] =
    Field1(
      Lens((_: (A1, A2))._1)(a => t => t.copy(_1 = a))
    )

  implicit def tuple3Field1[A1, A2, A3]: Field1[(A1, A2, A3), A1] =
    Field1(
      Lens((_: (A1, A2, A3))._1)(a => t => t.copy(_1 = a))
    )

  implicit def tuple4Field1[A1, A2, A3, A4]: Field1[(A1, A2, A3, A4), A1] =
    Field1(
      Lens((_: (A1, A2, A3, A4))._1)(a => t => t.copy(_1 = a))
    )

  implicit def tuple5Field1[A1, A2, A3, A4, A5]: Field1[(A1, A2, A3, A4, A5), A1] =
    Field1(
      Lens((_: (A1, A2, A3, A4, A5))._1)(a => t => t.copy(_1 = a))
    )

  implicit def tuple6Field1[A1, A2, A3, A4, A5, A6]: Field1[(A1, A2, A3, A4, A5, A6), A1] =
    Field1(
      Lens((_: (A1, A2, A3, A4, A5, A6))._1)(a => t => t.copy(_1 = a))
    )

  /** *********************************************************************************************
    */
  /** Cats instances */
  /** *********************************************************************************************
    */
  import cats.data.OneAnd

  implicit def oneAndField1[T[_], A]: Field1[OneAnd[T, A], A] =
    Field1(
      Lens[OneAnd[T, A], A](_.head)(a => oneAnd => oneAnd.copy(head = a))
    )
}
