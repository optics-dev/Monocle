package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines a [[Lens]] from an `S` to its fourth element of type `A`
 * @tparam S source of [[Lens]]
 * @tparam A target of [[Lens]], `A` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Field4[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Field4[S, A] extends Serializable {
  def fourth: Lens[S, A]
}

object Field4 extends Field4Functions {
  /** lift an instance of [[Field4]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field4[A, B]): Field4[S, B] = new Field4[S, B] {
    override def fourth: Lens[S, B] =
      iso composeLens ev.fourth
  }
}

trait Field4Functions {
  def fourth[S, A](implicit ev: Field4[S, A]): Lens[S, A] = ev.fourth
}
