package monocle.law

import monocle.Iso
import monocle.internal.IsEq

import scalaz.Id._

class IsoLaws[S, A](iso: Iso[S, A]) {
  import IsEq.syntax

  def roundTripOneWay(s: S): IsEq[S] =
    (iso.reverseGet _ compose iso.get)(s) <==> s

  def roundTripOtherWay(a: A): IsEq[A] =
    (iso.get _ compose iso.reverseGet)(a) <==> a
  
  def modifyIdentity(s: S): IsEq[S] =
    iso.modify(identity)(s) <==> s

  def modifyFId(s: S): IsEq[S] =
    iso.modifyF[Id](id.point[A](_))(s) <==> s
}