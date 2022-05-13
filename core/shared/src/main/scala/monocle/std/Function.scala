package monocle.std

import monocle.Iso

object function extends FunctionOptics

trait FunctionOptics {
  final def flip[A, B, C]: Iso[A => B => C, B => A => C] =
    Iso(flipped[A, B, C])(flipped)

  final def flipped[A, B, C]: (A => B => C) => B => A => C =
    f => a => b => f(b)(a)
}
