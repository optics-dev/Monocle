package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines a [[Lens]] from an `S` to its sixth element of type `A`
 * @tparam S source of [[Lens]]
 * @tparam A target of [[Lens]], `A` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Field6[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Field6[S, A] extends Serializable {
  def sixth: Lens[S, A]
}

object Field6 extends Field6Functions {
  /** lift an instance of [[Field6]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field6[A, B]): Field6[S, B] = new Field6[S, B] {
    override def sixth: Lens[S, B] =
      iso composeLens ev.sixth
  }
}

trait Field6Functions {
  def sixth[S, A](implicit ev: Field6[S, A]): Lens[S, A] = ev.sixth
}