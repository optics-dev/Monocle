package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound
import scala.collection.immutable.SortedMap

/**
 * Typeclass that defines a [[Lens]] from an `S` to an `A` at an index `I`
 * @tparam S source of [[Lens]]
 * @tparam I index
 * @tparam A target of [[Lens]], `A` is supposed to be unique for a given pair `(S, I)`
 */
@implicitNotFound("Could not find an instance of At[${S},${I},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class At[S, I, A] extends Serializable {
  def at(i: I): Lens[S, A]
}

trait AtFunctions {
  def at[S, I, A](i: I)(implicit ev: At[S, I, A]): Lens[S, A] = ev.at(i)

  /** delete a value associated with a key in a Map-like container */
  def remove[S, I, A](i: I)(s: S)(implicit ev: At[S, I, Option[A]]): S =
    ev.at(i).set(None)(s)
}

object At extends AtFunctions {
  def apply[S, I, A](get: I => S => A)(set: I => A => S => S): At[S, I, A] =
    new At[S, I, A] {
      def at(i: I): Lens[S, A] = Lens(get(i))(set(i))
    }

  /** lift an instance of [[At]] using an [[Iso]] */
  def fromIso[S, U, I, A](iso: Iso[S, U])(implicit ev: At[U, I, A]): At[S, I, A] = new At[S, I, A]{
    def at(i: I): Lens[S, A] =
      iso composeLens ev.at(i)
  }

  /************************************************************************************************/
  /** Std instances                                                                               */
  /************************************************************************************************/

  implicit def atSortedMap[K, V]: At[SortedMap[K, V], K, Option[V]] = new At[SortedMap[K, V], K, Option[V]]{
    def at(i: K) = Lens{m: SortedMap[K, V] => m.get(i)}(optV => map => optV.fold(map - i)(v => map + (i -> v)))
  }

  implicit def atSet[A]: At[Set[A], A, Boolean] = new At[Set[A], A, Boolean] {
    def at(a: A) = Lens[Set[A], Boolean](_.contains(a))(b => set => if(b) set + a else set - a)
  }
}
