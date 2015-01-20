package monocle.macros

import monocle.Lens
import monocle.macros.internal.MacroImpl

class Lenser[A] {
  def apply[B](field: A => B): Lens[A, B] = macro MacroImpl.genLens_impl[A, B]
}

object Lenser {
  @deprecated("use GenLens", "1.1.0")
  def apply[A] = new Lenser[A]
}


