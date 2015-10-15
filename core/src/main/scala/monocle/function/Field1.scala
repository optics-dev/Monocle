package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines a [[Lens]] from an `S` to its first element of type `A`
 * @tparam S source of [[Lens]]
 * @tparam A target of [[Lens]], `A` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Field1[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field1[S, A] extends Serializable {
  def first: Lens[S, A]
}

object Field1 extends Field1Functions {
  /** lift an instance of [[Field1]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field1[A, B]): Field1[S, B] = new Field1[S, B] {
    override def first: Lens[S, B] =
      iso composeLens ev.first
  }
}

trait Field1Functions {
  def first[S, A](implicit ev: Field1[S, A]): Lens[S, A] = ev.first
}
