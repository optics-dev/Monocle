package monocle.macros

import monocle.Focus

object GenPrism {
  def apply[Source, Target <: Source] = Focus[Source](_.as[Target])
}
