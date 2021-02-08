package monocle.macros

import monocle.Lens
import monocle.Focus.MkFocus

object GenLens {
  @deprecated("use monocle.Focus", since = "3.0.0-M1")
  def apply[A] = new MkFocus[A]
}
