package monocle.macros

import monocle.Prism

object GenPrism {
  /** generate a [[Prism]] between `S` and a subtype `A` of `S` */
  def apply[S, A <: S]: Prism[S, A] =
    Prism.as[S, A]
}
