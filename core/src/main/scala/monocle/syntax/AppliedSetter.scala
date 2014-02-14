package monocle.syntax

import monocle.Setter


trait AppliedSetter[S, T, A, B] { self =>
  val from: S
  def _setter: Setter[S, T, A, B]

  def set(newValue: B): T = _setter.set(from, newValue)

  def modify(f: A => B): T = _setter.modify(from, f)

  def oo[C, D](other: Setter[A, B, C, D]): AppliedSetter[S, T, C, D] = new AppliedSetter[S, T, C, D]{
    val from   = self.from
    val _setter = self._setter compose other
  }

}