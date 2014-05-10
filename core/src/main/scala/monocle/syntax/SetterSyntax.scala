package monocle.syntax

import monocle.Setter

private[syntax] trait SetterSyntax {
  implicit def toPartialApplySetterOps[S](value: S): PartialApplySetterOps[S] = new PartialApplySetterOps(value)
}

private[syntax] trait PartialApplySetter[S, T, A, B] { self =>
  def from: S
  def _setter: Setter[S, T, A, B]

  def set(newValue: B): T = _setter.set(from, newValue)

  def modify(f: A => B): T = _setter.modify(from, f)

  def composeSetter[C, D](other: Setter[A, B, C, D]): PartialApplySetter[S, T, C, D] = new PartialApplySetter[S, T, C, D] {
    val from = self.from
    val _setter = self._setter compose other
  }
}

private[syntax] final class PartialApplySetterOps[S](value: S) {
  def partialApplySetter[T, A, B](setter: Setter[S, T, A, B]): PartialApplySetter[S, T, A, B] = new PartialApplySetter[S, T, A, B] {
    val from: S = value
    def _setter: Setter[S, T, A, B] = setter
  }
}
