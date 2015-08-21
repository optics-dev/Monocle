package monocle.law

import monocle.Prism
import monocle.internal.IsEq

import scalaz.Id._

class PrismLaws[S, A](prism: Prism[S, A]) {
  import IsEq.syntax

  def partialRoundTripOneWay(s: S): IsEq[S] =
    prism.matching(s).fold(identity, prism.reverseGet) <==> s
  
  def roundTripOtherWay(a: A): IsEq[Option[A]] =
    prism.getOption(prism.reverseGet(a)) <==> Some(a)

  def modifyIdentity(s: S): IsEq[S] =
    prism.modify(identity)(s) <==> s

  def modifyFId(s: S): IsEq[S] =
    prism.modifyF[Id](id.point[A](_))(s) <==> s
  
  def modifyOptionIdentity(s: S): IsEq[Option[S]] =
    prism.modifyOption(identity)(s) <==> prism.getOption(s).map(_ => s)
}