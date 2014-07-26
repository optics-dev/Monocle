package monocle.syntax

import monocle.Setter

object setter extends SetterSyntax

private[syntax] trait SetterSyntax {
  implicit def toApplySetterOps[S](value: S): ApplySetterOps[S] = new ApplySetterOps(value)
}

private[syntax] trait ApplySetter[S, T, A, B] { self =>
  def from: S
  def _setter: Setter[S, T, A, B]

  def set(newValue: B): T = _setter.set(from, newValue)

  def modify(f: A => B): T = _setter.modify(from, f)

  def composeSetter[C, D](other: Setter[A, B, C, D]): ApplySetter[S, T, C, D] = new ApplySetter[S, T, C, D] {
    val from = self.from
    val _setter = self._setter composeSetter other
  }
}

private[syntax] final class ApplySetterOps[S](value: S) {
  def applySetter[T, A, B](setter: Setter[S, T, A, B]): ApplySetter[S, T, A, B] = new ApplySetter[S, T, A, B] {
    val from: S = value
    def _setter: Setter[S, T, A, B] = setter
  }
}
