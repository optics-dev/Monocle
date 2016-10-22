package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines a [[Lens]] from an `S` to its third element of type `A`
 * @tparam S source of [[Lens]]
 * @tparam A target of [[Lens]], `A` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Field3[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Field3[S, A] extends Serializable {
  def third: Lens[S, A]
}

object Field3 extends Field3Functions {
  /** lift an instance of [[Field3]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field3[A, B]): Field3[S, B] = new Field3[S, B] {
    override def third: Lens[S, B] =
      iso composeLens ev.third
  }
}

trait Field3Functions {
  def third[S, A](implicit ev: Field3[S, A]): Lens[S, A] = ev.third
}