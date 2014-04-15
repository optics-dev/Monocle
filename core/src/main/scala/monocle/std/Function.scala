package monocle.std

import monocle._
import monocle.function.Curry

object function extends FunctionInstances

trait FunctionInstances {

  def curry[F, G](implicit evidence: Curry[F, G]): SimpleIso[F, G] = evidence.curry

  def uncurry[F, G](implicit evidence: Curry[F, G]): SimpleIso[G, F] = curry.reverse

  def flip[A, B, C]: SimpleIso[A => B => C, B => A => C] =
    SimpleIso(flipped, flipped)

  def flipped[A, B, C]: (A => B => C) => (B => A => C) =
    f => a => b => f(b)(a)
}
