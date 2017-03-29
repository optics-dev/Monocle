package monocle.law

import monocle.Traversal
import monocle.internal.IsEq

case class TraversalLaws[S, A](traversal: Traversal[S, A]) {
  import IsEq.syntax

  def headOption(s: S): IsEq[Option[A]] =
    traversal.headOption(s) <==> traversal.getAll(s).headOption

  def modifyGetAll(s: S, f: A => A): IsEq[List[A]] =
    traversal.getAll(traversal.modify(f)(s)) <==> traversal.getAll(s).map(f)

  def setIdempotent(s: S, a: A): IsEq[S] =
    traversal.set(a)(traversal.set(a)(s)) <==> traversal.set(a)(s)

  def modifyIdentity(s: S): IsEq[S] =
    traversal.modify(identity)(s) <==> s

  def composeModify(s: S, f: A => A, g: A => A): IsEq[S] =
    traversal.modify(g)(traversal.modify(f)(s)) <==> traversal.modify(g compose f)(s)
}
