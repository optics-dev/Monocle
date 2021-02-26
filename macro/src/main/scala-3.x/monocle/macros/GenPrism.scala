package monocle.macros

import monocle.Focus

object GenPrism {
  transparent inline def apply[Source, Target <: Source] =
    Focus[Source](_.as[Target])
}