package monocle

import scalaz._, Scalaz._
import scalaz.syntax.tag._

import Indexable._

abstract class IPTraversal[I, S, T, A, B] {

  def modifyF[
      F[_]: Applicative,
      P[_, _]: Indexable[I, ?[_, _]]](
    f: P[A, F[B]])(s: S): F[T]

  def getAll(s: S): List[(I, A)] =
    modifyF[Const[List[(I, A)], ?], Indexed[I, ?, ?]](
      Indexed(i => a => Const(List((i, a)))))(s).getConst

  def modify(f: I => A => B)(s: S): T =
    modifyF[Id, Indexed[I, ?, ?]](Indexed(f))(s)

  def foldMap[M: Monoid](f: I => A => M)(s: S): M =
    modifyF[Const[M, ?], Indexed[I, ?, ?]](
      Indexed(i => a => Const(f(i)(a))))(s).getConst

  def indices(s: S): List[I] =
    getAll(s).map(_._1)

  def isEmpty(s: S): Boolean =
    foldMap(_ => _ => false.conjunction)(s).unwrap

  def nonEmpty(s: S): Boolean =
    !isEmpty(s)
}

object IPTraversal {

  def both[A, B] = new IPTraversal[Boolean, (A, A), (B, B), A, B] {
    def modifyF[
          F[_]: Applicative,
          P[_, _]: Indexable[Boolean, ?[_, _]]](
        f: P[A, F[B]])(s: (A, A)): F[(B, B)] =
      index(f)(true)(s._1).tuple(index(f)(false)(s._2))
  }
}

trait Indexable[I, P[_, _]] {
  def index[A, B](pab: P[A, B])(i: I)(a: A): B
}

object Indexable {

  implicit def functionIndexable[I]: Indexable[I, ? => ?] =
    new Indexable[I, ? => ?] {
      def index[A, B](f: A => B)(i: I)(a: A) = f(a)
    }

  case class Indexed[I, A, B](runIndexed: I => A => B)

  implicit def indexedIndexable[I]: Indexable[I, Indexed[I, ?, ?]] =
    new Indexable[I, Indexed[I, ?, ?]] {
      def index[A, B](ix: Indexed[I, A, B])(i: I)(a: A) = ix.runIndexed(i)(a)
    }

  def index[I, P[_, _], A, B](
      pab: P[A, B])(
      i: I)(
      a: A)(implicit
      I: Indexable[I, P]): B =
    I.index(pab)(i)(a)
}
