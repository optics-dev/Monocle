package monocle.macros

import Monocle.Focus.MkFocus

object GenPrism {
  def apply[Source, Target] = new MkFocus[Source](_.as[Target])
}
