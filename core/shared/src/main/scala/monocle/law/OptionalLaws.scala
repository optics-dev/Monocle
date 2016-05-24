package monocle.law

import monocle.Optional
import monocle.internal.IsEq

import scalaz.Id._

case class OptionalLaws[S, A](optional: Optional[S, A]) {
  import IsEq.syntax

  def getOptionSet(s: S): IsEq[S] =
    optional.getOrModify(s).fold(identity, optional.set(_)(s)) <==> s

  def setGetOption(s: S, a: A): IsEq[Option[A]] =
    optional.getOption(optional.set(a)(s)) <==> optional.getOption(s).map(_ => a)

  def setIdempotent(s: S, a: A): IsEq[S] =
    optional.set(a)(optional.set(a)(s)) <==> optional.set(a)(s)

  def modifyIdentity(s: S): IsEq[S] =
    optional.modify(identity)(s) <==> s

  def modifyFId(s: S): IsEq[S] =
    optional.modifyF[Id](id.point[A](_))(s) <==> s

  def modifyOptionIdentity(s: S): IsEq[Option[S]] =
    optional.modifyOption(identity)(s) <==> optional.getOption(s).map(_ => s)

  def composeModify(s: S, f: A => A, g: A => A): IsEq[S] =
    optional.modify(g)(optional.modify(f)(s)) <==> optional.modify(g compose f)(s)

  def consistentModify(s: S, a: A): IsEq[S] =
    optional.modify(_ => a)(s) <==> optional.set(a)(s)
}