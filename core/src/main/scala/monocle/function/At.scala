package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines a [[Lens]] from an `S` to an `A` at an index `I`
 * @tparam S source of [[Lens]]
 * @tparam I index
 * @tparam A target of [[Lens]], `A` is supposed to be unique for a given pair `(S, I)`
 */
@implicitNotFound("Could not find an instance of At[${S},${I},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait At[S, I, A] extends Serializable {
  def at(i: I): Lens[S, A]
}


object At extends AtFunctions {
  /** lift an instance of [[At]] using an [[Iso]] */
  def fromIso[S, U, I, A](iso: Iso[S, U])(implicit ev: At[U, I, A]): At[S, I, A] = new At[S, I, A]{
    override def at(i: I): Lens[S, A] =
      iso composeLens ev.at(i)
  }
}

trait AtFunctions {
  def at[S, I, A](i: I)(implicit ev: At[S, I, A]): Lens[S, A] = ev.at(i)
}
