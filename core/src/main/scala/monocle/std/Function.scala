package monocle.std

import monocle._
import monocle.function.Curry

object function extends FunctionFunctions with FunctionInstances

trait FunctionFunctions {

  def flip[A, B, C]: SimpleIso[A => B => C, B => A => C] =
    SimpleIso(flipped, flipped)

  def flipped[A, B, C]: (A => B => C) => (B => A => C) =
    f => a => b => f(b)(a)
}

/**
 * We do a trait inheritance hierarchy in order to solve ambiguous implicit resolution.
 * The traits higher up (FunctionInstances0) will get higher priority in implicit resolution.
 **/
trait FunctionInstances extends FunctionInstances1 {
  implicit def curry5[A, B, C, D, E, F] = new Curry[(A, B, C, D, E) => F, A => B => C => D => E => F] {
    def curry = SimpleIso(_.curried, f => Function.uncurried(f))
  }
}

trait FunctionInstances1 extends FunctionInstances2 {
  implicit def curry4[A, B, C, D, E] = new Curry[(A, B, C, D) => E, A => B => C => D => E] {
    def curry = SimpleIso(_.curried, f => Function.uncurried(f))
  }
}

trait FunctionInstances2 extends FunctionInstances3 {
  implicit def curry3[A, B, C, D] = new Curry[(A, B, C) => D, A => B => C => D] {
    def curry = SimpleIso(_.curried, f => Function.uncurried(f))
  }
}

trait FunctionInstances3 {
  implicit def curry2[A, B, C] = new Curry[(A, B) => C, A => B => C] {
    def curry = SimpleIso(_.curried, f => Function.uncurried(f))
  }
}