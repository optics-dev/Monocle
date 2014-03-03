package monocle.syntax

import monocle.Getter

trait AppliedGetter[S, A] { self =>

  val from: S
  def _getter: Getter[S, A]

  def get: A = _getter.get(from)

  def oo[B](other: Getter[A, B]): AppliedGetter[S, B] = new AppliedGetter[S, B] {
    val from: S = self.from
    val _getter: Getter[S, B] = self._getter compose other
  }

}
