package monocle.law

import monocle.Setter
import monocle.internal.IsEq

case class SetterLaws[S, A](setter: Setter[S, A]) {
  import IsEq.syntax

  def replaceIdempotent(s: S, a: A): IsEq[S] =
    setter.replace(a)(setter.replace(a)(s)) <==> setter.replace(a)(s)

  def modifyIdentity(s: S): IsEq[S] =
    setter.modify(identity)(s) <==> s

  def composeModify(s: S, f: A => A, g: A => A): IsEq[S] =
    setter.modify(g)(setter.modify(f)(s)) <==> setter.modify(g compose f)(s)

  def consistentReplaceModify(s: S, a: A): IsEq[S] =
    setter.modify(_ => a)(s) <==> setter.replace(a)(s)
}
