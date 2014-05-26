package monocle.syntax

import monocle.Getter

object getter extends GetterSyntax

private[syntax] trait GetterSyntax {
  implicit def toApplyGetterOps[S](value: S): ApplyGetterOps[S] = new ApplyGetterOps(value)
}

private[syntax] trait ApplyGetter[S, A] { self =>
  def from: S
  def _getter: Getter[S, A]

  def get: A = _getter.get(from)

  def composeGetter[B](other: Getter[A, B]): ApplyGetter[S, B] = new ApplyGetter[S, B] {
    val from: S = self.from
    val _getter: Getter[S, B] = self._getter compose other
  }

}

private[syntax] final class ApplyGetterOps[S](value: S) {
  def applyGetter[A](getter: Getter[S, A]): ApplyGetter[S, A] = new ApplyGetter[S, A] {
    val from: S = value
    def _getter: Getter[S, A] = getter
  }
}
