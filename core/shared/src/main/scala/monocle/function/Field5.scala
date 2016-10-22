package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines a [[Lens]] from an `S` to its fifth element of type `A`
 * @tparam S source of [[Lens]]
 * @tparam A target of [[Lens]], `A` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Field5[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Field5[S, A] extends Serializable {
  def fifth: Lens[S, A]
}

object Field5 extends Field5Functions {
  /** lift an instance of [[Field5]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field5[A, B]): Field5[S, B] = new Field5[S, B] {
    override def fifth: Lens[S, B] =
      iso composeLens ev.fifth
  }
}

trait Field5Functions {
  def fifth[S, A](implicit ev: Field5[S, A]): Lens[S, A] = ev.fifth
}
