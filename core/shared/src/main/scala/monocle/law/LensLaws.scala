package monocle.law

import monocle.Lens
import monocle.internal.IsEq

import cats.data.Const
import cats.Id

case class LensLaws[S, A](lens: Lens[S, A]) {
  import IsEq.syntax

  def getReplace(s: S): IsEq[S] =
    lens.replace(lens.get(s))(s) <==> s

  def replaceGet(s: S, a: A): IsEq[A] =
    lens.get(lens.replace(a)(s)) <==> a

  def replaceIdempotent(s: S, a: A): IsEq[S] =
    lens.replace(a)(lens.replace(a)(s)) <==> lens.replace(a)(s)

  def modifyIdentity(s: S): IsEq[S] =
    lens.modify(identity)(s) <==> s

  def composeModify(s: S, f: A => A, g: A => A): IsEq[S] =
    lens.modify(g)(lens.modify(f)(s)) <==> lens.modify(g compose f)(s)

  def consistentReplaceModify(s: S, a: A): IsEq[S] =
    lens.replace(a)(s) <==> lens.modify(_ => a)(s)

  def consistentModifyModifyId(s: S, f: A => A): IsEq[S] =
    lens.modify(f)(s) <==> lens.modifyF[Id](f)(s)

  def consistentGetModifyId(s: S): IsEq[A] =
    lens.get(s) <==> lens.modifyF[Const[A, *]](Const(_))(s).getConst
}
