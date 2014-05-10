package monocle.syntax

import monocle.Getter

private[syntax] trait GetterSyntax {
  implicit def toPartialApplyGetterOps[S](value: S): PartialApplyGetterOps[S] = new PartialApplyGetterOps(value)
}

private[syntax] trait PartialApplyGetter[S, A] { self =>
  def from: S
  def _getter: Getter[S, A]

  def get: A = _getter.get(from)

  def composeGetter[B](other: Getter[A, B]): PartialApplyGetter[S, B] = new PartialApplyGetter[S, B] {
    val from: S = self.from
    val _getter: Getter[S, B] = self._getter compose other
  }

}

private[syntax] final class PartialApplyGetterOps[S](value: S) {
  def partialApplyGetter[A](getter: Getter[S, A]): PartialApplyGetter[S, A] = new PartialApplyGetter[S, A] {
    val from: S = value
    def _getter: Getter[S, A] = getter
  }
}
