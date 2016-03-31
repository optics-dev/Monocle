package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines a [[Lens]] from an `S` to its second element of type `A`
 * @tparam S source of [[Lens]]
 * @tparam A target of [[Lens]], `A` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Field2[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field2[S, A] extends Serializable {
  def second: Lens[S, A]
}

object Field2 extends Field2Functions {
  /** lift an instance of [[Field2]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field2[A, B]): Field2[S, B] = new Field2[S, B] {
    def second: Lens[S, B] =
      iso composeLens ev.second
  }
}

trait Field2Functions {
  def second[S, A](implicit ev: Field2[S, A]): Lens[S, A] = ev.second
}
