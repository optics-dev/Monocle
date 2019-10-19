package monocle.function

import monocle.{Lens, Optional, Prism}


trait Cons[S] {
  type A

  def cons: Prism[S, (A, S)]

  def headOption: Optional[S, A] = cons composeLens Lens.first
  def tailOption: Optional[S, S] = cons composeLens Lens.second
}

object Cons {
  type Aux[S, A0] = Cons[S] { type A = A0 }
}
