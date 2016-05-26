package monocle.law

import monocle.Setter
import monocle.internal.IsEq

case class SetterLaws[S, A](setter: Setter[S, A]) {
  import IsEq.syntax

  def setIdempotent(s: S, a: A): IsEq[S] =
    setter.set(a)(setter.set(a)(s)) <==> setter.set(a)(s)

  def modifyIdentity(s: S): IsEq[S] =
    setter.modify(identity)(s) <==> s

  def composeModify(s: S, f: A => A, g: A => A): IsEq[S] =
    setter.modify(g)(setter.modify(f)(s)) <==> setter.modify(g compose f)(s)

  def consistentSetModify(s: S, a: A): IsEq[S] =
    setter.modify(_ => a)(s) <==> setter.set(a)(s)
}