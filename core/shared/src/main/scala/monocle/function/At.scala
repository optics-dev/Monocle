package monocle.function

import cats.Eq
import monocle.{Iso, Lens}
import monocle.std.option.withDefault

import scala.annotation.implicitNotFound
import scala.collection.immutable.{ListMap, SortedMap}

/**
  * Typeclass that defines a [[Lens]] from an `S` to an `A` at an index `I`
  * @tparam S source of [[Lens]]
  * @tparam I index
  * @tparam A target of [[Lens]], `A` is supposed to be unique for a given pair `(S, I)`
  */
@implicitNotFound(
  "Could not find an instance of At[${S},${I},${A}], please check Monocle instance location policy to " + "find out which import is necessary"
)
abstract class At[S, I, A] extends Serializable {
  def at(i: I): Lens[S, A]
}

trait AtFunctions {
  def at[S, I, A](i: I)(implicit ev: At[S, I, A]): Lens[S, A] = ev.at(i)

  /**
    * Creates a Lens that zooms into an index `i` inside `S`.
    * If `S` doesn't have any data at this index, `atOrElse` insert `defaultValue`.
    * {{{
    * val counters = Map("id1" -> 4, "id2" -> 2)
    * def mapDefaultTo0(index: String): Lens[Map[String, Int], Int] =
    *   atOrElse(index)(0)
    *
    * mapDefaultTo0("id1").get(counters) == 4
    * mapDefaultTo0("id3").get(counters) == 0
    *
    * mapDefaultTo0("id1").modify(_ + 1)(counters) == Map("id1" -> 5, "id2" -> 2)
    * mapDefaultTo0("id3").modify(_ + 1)(counters) == Map("id1" -> 4, "id2" -> 2, "id3" -> 1)
    * }}}
    *
    * `atOrElse`` is a valid Lens only if `defaultValue` is not part of `S`.
    * For example, `Map("id" -> 0)` breaks the get-set property of Lens:
    * {{{
    * val counters = Map("id" -> 0)
    * val fromGet  = mapDefaultTo0("id").get(counters)   // 0
    * val afterSet = mapDefaultTo0("id").set(0)(fromGet) // Map()
    *
    * counters != afterSet
    * }}}
    *
    * @see monocle.std.option.withDefault
    */
  def atOrElse[S, I, A: Eq](i: I)(defaultValue: A)(implicit ev: At[S, I, Option[A]]): Lens[S, A] =
    ev.at(i) composeIso withDefault(defaultValue)

  /** delete a value associated with a key in a Map-like container */
  def remove[S, I, A](i: I)(s: S)(implicit ev: At[S, I, Option[A]]): S =
    ev.at(i).set(None)(s)
}

object At extends AtFunctions {
  def apply[S, I, A](lens: I => Lens[S, A]): At[S, I, A] = (i: I) => lens(i)

  def apply[S, I, A](get: I => S => A)(set: I => A => S => S): At[S, I, A] =
    (i: I) => Lens(get(i))(set(i))

  /** lift an instance of [[At]] using an [[Iso]] */
  def fromIso[S, U, I, A](iso: Iso[S, U])(implicit ev: At[U, I, A]): At[S, I, A] =
    At(
      iso composeLens ev.at(_)
    )

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */
  implicit def atSortedMap[K, V]: At[SortedMap[K, V], K, Option[V]] =
    At(i => Lens((_: SortedMap[K, V]).get(i))(optV => map => optV.fold(map - i)(v => map + (i -> v))))

  implicit def atListMap[K, V]: At[ListMap[K, V], K, Option[V]] =
    At(i => Lens((_: ListMap[K, V]).get(i))(optV => map => optV.fold(map - i)(v => map + (i -> v))))

  implicit def atMap[K, V]: At[Map[K, V], K, Option[V]] =
    At(i => Lens((_: Map[K, V]).get(i))(optV => map => optV.fold(map - i)(v => map + (i -> v))))

  implicit def atSet[A]: At[Set[A], A, Boolean] =
    At(a => Lens((_: Set[A]).contains(a))(b => set => if (b) set + a else set - a))
}
