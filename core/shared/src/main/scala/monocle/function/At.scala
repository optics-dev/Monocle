package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound
import scala.collection.immutable.{ListMap, SortedMap}

/** Typeclass that defines a [[Lens]] from an `S` to an `A` at an index `I`
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

abstract class AtSingleton[S, I <: Singleton, A] extends At[S, I, A] {
  def at(i: I): Lens[S, A]
}

trait AtFunctions {
  def at[S, I, A](i: I)(implicit ev: At[S, I, A]): Lens[S, A] = ev.at(i)

  /** delete a value associated with a key in a Map-like container */
  def remove[S, I, A](i: I)(s: S)(implicit ev: At[S, I, Option[A]]): S =
    ev.at(i).set(None)(s)
}

trait AtSingletonFunctions {
  def at[S, I <: Singleton, A](i: I)(implicit ev: AtSingleton[S, I, A]): Lens[S, A] =
    ev.at(i)
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

  /** **************
    */
  /** Std instances */
  /** **************
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

object AtSingleton extends AtSingletonFunctions {
  def apply[S, I <: Singleton, A](lens: I => Lens[S, A]): AtSingleton[S, I, A] = (i: I) => lens(i)

  implicit def at1_tuple2[A1, A2]: AtSingleton[(A1, A2), 1, A1] =
    AtSingleton(_ => Lens[(A1, A2), A1](_._1)(x => _.copy(_1 = x)))

  implicit def at2_tuple2[A1, A2]: AtSingleton[(A1, A2), 2, A2] =
    AtSingleton(_ => Lens[(A1, A2), A2](_._2)(x => _.copy(_2 = x)))

  implicit def at1_tuple3[A1, A2, A3]: AtSingleton[(A1, A2, A3), 1, A1] =
    AtSingleton(_ => Lens[(A1, A2, A3), A1](_._1)(x => _.copy(_1 = x)))

  implicit def at2_tuple3[A1, A2, A3]: AtSingleton[(A1, A2, A3), 2, A2] =
    AtSingleton(_ => Lens[(A1, A2, A3), A2](_._2)(x => _.copy(_2 = x)))

  implicit def at3_tuple3[A1, A2, A3]: AtSingleton[(A1, A2, A3), 3, A3] =
    AtSingleton(_ => Lens[(A1, A2, A3), A3](_._3)(x => _.copy(_3 = x)))
}
