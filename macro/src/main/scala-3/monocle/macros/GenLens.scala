package monocle.macros

import monocle.Focus.MkFocus

object GenLens {
  def apply[A] = new MkFocus[A]
}
