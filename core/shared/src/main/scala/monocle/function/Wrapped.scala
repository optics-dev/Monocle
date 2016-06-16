package monocle.function

import monocle.Iso

import scalaz.Functor

import scala.annotation.implicitNotFound

/**
  * Typeclass that defines an [[Iso]] from an `S` to an `A`
  * @tparam S source of [[Iso]]
  * @tparam A target of [[Iso]], `A` is supposed to be unique for a given `S`
  */
@implicitNotFound("Could not find an instance of Wrapped[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Wrapped[S, A] extends Serializable {
  def wrapped: Iso[S, A]
}

trait WrappedFunctions {
  def wrapped[S, A](implicit ev: Wrapped[S, A]): Iso[S, A] = ev.wrapped

  def unwrapped[S, A](implicit ev: Wrapped[S, A]): Iso[A, S] = ev.wrapped.reverse
}
