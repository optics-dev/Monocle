package monocle.law

import monocle.Iso
import monocle.internal.IsEq

import cats.data.Const
import cats.Id

case class IsoLaws[S, A](iso: Iso[S, A]) {

  import IsEq.syntax

  def roundTripOneWay(s: S): IsEq[S] =
    (iso.reverseGet _ compose iso.get) (s) <==> s

  def roundTripOtherWay(a: A): IsEq[A] =
    (iso.get _ compose iso.reverseGet) (a) <==> a

  def modifyIdentity(s: S): IsEq[S] =
    iso.modify(identity)(s) <==> s

  def composeModify(s: S, f: A => A, g: A => A): IsEq[S] =
    iso.modify(g)(iso.modify(f)(s)) <==> iso.modify(g compose f)(s)

  def consistentSetModify(s: S, a: A): IsEq[S] =
    iso.set(a)(s) <==> iso.modify(_ => a)(s)

  def consistentModifyModifyId(s: S, f: A => A): IsEq[S] =
    iso.modify(f)(s) <==> iso.modifyF[Id](f)(s)

  def consistentGetModifyId(s: S): IsEq[A] =
    iso.get(s) <==> iso.modifyF[Const[A, ?]](Const(_))(s).getConst

}
