package monocle.law

import monocle.Iso
import monocle.internal.IsEq

import scalaz.Id._

case class IsoLaws[S, A](iso: Iso[S, A]) {
  import IsEq.syntax

  def roundTripOneWay(s: S): IsEq[S] =
    (iso.reverseGet _ compose iso.get)(s) <==> s

  def roundTripOtherWay(a: A): IsEq[A] =
    (iso.get _ compose iso.reverseGet)(a) <==> a
  
  def modifyIdentity(s: S): IsEq[S] =
    iso.modify(identity)(s) <==> s

  def modifyFId(s: S): IsEq[S] =
    iso.modifyF[Id](id.point[A](_))(s) <==> s

  def composeModify(s: S, f: A => A, g: A => A): IsEq[S] =
    iso.modify(g)(iso.modify(f)(s)) <==> iso.modify(g compose f)(s)

  def consistentModify(s: S, a: A): IsEq[S] =
    iso.modify(_ => a)(s) <==> iso.set(a)(s)
}