package monocle.macros

import Monocle.Focus.MkFocus

object GenPrism {
  def apply[A, B] = new MkFocus[A](_.as[B])
}
