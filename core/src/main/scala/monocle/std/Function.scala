package monocle.std

import monocle.Iso
import monocle.function.Curry

object function extends FunctionOptics

trait FunctionOptics  extends FunctionOptics1  {

  final def flip[A, B, C]: Iso[A => B => C, B => A => C] =
    Iso(flipped[A, B, C])(flipped)

  final def flipped[A, B, C]: (A => B => C) => (B => A => C) =
    f => a => b => f(b)(a)

  implicit def curry5[A, B, C, D, E, F] = new Curry[(A, B, C, D, E) => F, A => B => C => D => E => F] {
    def curry = Iso((_: (A, B, C, D, E) => F).curried)(f => Function.uncurried(f))
  }
}

trait FunctionOptics1 extends FunctionOptics2 {
  implicit def curry4[A, B, C, D, E] = new Curry[(A, B, C, D) => E, A => B => C => D => E] {
    def curry = Iso((_: (A, B, C, D) => E).curried)(f => Function.uncurried(f))
  }
}

trait FunctionOptics2 extends FunctionOptics3 {
  implicit def curry3[A, B, C, D] = new Curry[(A, B, C) => D, A => B => C => D] {
    def curry = Iso((_: (A, B, C) => D).curried)(f => Function.uncurried(f))
  }
}

trait FunctionOptics3 {
  implicit def curry2[A, B, C] = new Curry[(A, B) => C, A => B => C] {
    def curry = Iso((_: (A, B) => C).curried)(f => Function.uncurried(f))
  }
}