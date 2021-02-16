package monocle.macros

import monocle.Focus
import monocle.Focus._

object GenPrism {
  transparent inline def apply[Source, Target <: Source] = 
    Focus[Source](_.as[Target])
}
