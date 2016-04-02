package monocle.law

import monocle.Lens
import monocle.internal.IsEq

import scalaz.Id._

class LensLaws[S, A](lens: Lens[S, A]) {
  import IsEq.syntax

  def getSet(s: S): IsEq[S] =
    lens.set(lens.get(s))(s) <==> s

  def setGet(s: S, a: A): IsEq[A] =
    lens.get(lens.set(a)(s)) <==> a

  def setIdempotent(s: S, a: A): IsEq[S] =
    lens.set(a)(lens.set(a)(s)) <==> lens.set(a)(s)

  def modifyIdentity(s: S): IsEq[S] =
    lens.modify(identity)(s) <==> s

  def modifyFId(s: S): IsEq[S] =
    lens.modifyF[Id](id.point[A](_))(s) <==> s
}
