package monocle.internal

import scalaz.{Functor, Profunctor}
import scalaz.std.function._

/**
 * Generalizing upstar of a strong Functor
 */
trait Strong[P[_, _]] extends Profunctor[P] {
  def first[A, B, C] (pab: P[A, B]): P[(A, C), (B, C)]
  def second[A, B, C](pab: P[A, B]): P[(C, A), (C, B)]
}

object Strong {

  def apply[P[_, _]](implicit ev: Strong[P]): Strong[P] = ev

  implicit val function1Strong = new Strong[Function1]{
    def first[A, B, C] (f: A => B): ((A, C)) => (B, C) =
      ac => ac.copy(_1 = f(ac._1))

    def second[A, B, C](f: A => B): ((C, A)) => (C, B) =
      ca => ca.copy(_2 = f(ca._2))

    def mapfst[A, B, C](fab: A => B)(f: C => A): C => B = Profunctor[Function1].mapfst(fab)(f)
    def mapsnd[A, B, C](fab: A => B)(f: B => C): A => C = Profunctor[Function1].mapsnd(fab)(f)
  }

  implicit def function1ToFunctorStrong[F[_]](implicit F: Functor[F]) = new Strong[({ type l[a, b] = a => F[b] })#l]{
    def first[A, B, C](f: A => F[B]): ((A, C)) => F[(B, C)] =
      ac => F.map(f(ac._1))(b => ac.copy(_1 = b))

    def second[A, B, C](f: A => F[B]): ((C, A)) => F[(C, B)] =
      ac => F.map(f(ac._2))(b => ac.copy(_2 = b))

    def mapfst[A, B, C](fab: A => F[B])(f: C => A): C => F[B] = fab compose f
    def mapsnd[A, B, C](fab: A => F[B])(f: B => C): A => F[C] = a => F.map(fab(a))(f)
  }

}
