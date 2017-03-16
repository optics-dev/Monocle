package monocle.law

import monocle.Prism
import monocle.internal.IsEq

import cats.Id
import cats.data.Const
import newts.FirstOption
import newts.syntax.all._

case class PrismLaws[S, A](prism: Prism[S, A]) {
  import IsEq.syntax

  def partialRoundTripOneWay(s: S): IsEq[S] =
    prism.getOrModify(s).fold(identity, prism.reverseGet) <==> s
  
  def roundTripOtherWay(a: A): IsEq[Option[A]] =
    prism.getOption(prism.reverseGet(a)) <==> Some(a)

  def modifyIdentity(s: S): IsEq[S] =
    prism.modify(identity)(s) <==> s

  def composeModify(s: S, f: A => A, g: A => A): IsEq[S] =
    prism.modify(g)(prism.modify(f)(s)) <==> prism.modify(g compose f)(s)

  def consistentSetModify(s: S, a: A): IsEq[S] =
    prism.set(a)(s) <==> prism.modify(_ => a)(s)

  def consistentModifyModifyId(s: S, f: A => A): IsEq[S] =
    prism.modify(f)(s) <==> prism.modifyF[Id](f)(s)

  def consistentGetOptionModifyId(s: S): IsEq[Option[A]] =
    prism.getOption(s) <==> prism.modifyF[Const[FirstOption[A], ?]](a => Const(Some(a).asFirstOption))(s).getConst.unwrap
}
