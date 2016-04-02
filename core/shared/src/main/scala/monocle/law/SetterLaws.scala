package monocle.law

import monocle.Setter
import monocle.internal.IsEq

class SetterLaws[S, A](setter: Setter[S, A]) {
  import IsEq.syntax

  def setIdempotent(s: S, a: A): IsEq[S] =
    setter.set(a)(setter.set(a)(s)) <==> setter.set(a)(s)

  def modifyIdentity(s: S): IsEq[S] =
    setter.modify(identity)(s) <==> s
}