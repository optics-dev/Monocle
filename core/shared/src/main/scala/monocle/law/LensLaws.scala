package monocle.law

import monocle.Lens
import monocle.internal.IsEq

import scalaz.Const
import scalaz.Id._

case class LensLaws[S, A](lens: Lens[S, A]) {
  import IsEq.syntax

  def getSet(s: S): IsEq[S] =
    lens.set(lens.get(s))(s) <==> s

  def setGet(s: S, a: A): IsEq[A] =
    lens.get(lens.set(a)(s)) <==> a

  def setIdempotent(s: S, a: A): IsEq[S] =
    lens.set(a)(lens.set(a)(s)) <==> lens.set(a)(s)

  def modifyIdentity(s: S): IsEq[S] =
    lens.modify(identity)(s) <==> s

  def composeModify(s: S, f: A => A, g: A => A): IsEq[S] =
    lens.modify(g)(lens.modify(f)(s)) <==> lens.modify(g compose f)(s)

  def consistentSetModify(s: S, a: A): IsEq[S] =
    lens.set(a)(s) <==> lens.modify(_ => a)(s)

  def consistentModifyModifyId(s: S, f: A => A): IsEq[S] =
    lens.modify(f)(s) <==> lens.modifyF(a => id.pure(f(a)))(s)

  def consistentGetModifyId(s: S): IsEq[A] =
    lens.get(s) <==> lens.modifyF[Const[A, ?]](Const(_))(s).getConst
}
