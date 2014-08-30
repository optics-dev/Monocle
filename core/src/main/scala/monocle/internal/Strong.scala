package monocle.internal

import scalaz.std.function._
import scalaz.{Functor, Kleisli, Profunctor}

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

  implicit def kleisliStrong[F[_]](implicit F: Functor[F]) = new Strong[({type λ[α,β] = Kleisli[F, α, β]})#λ]{

    def first[A, B, C](f: Kleisli[F, A, B]): Kleisli[F, (A, C), (B, C)] =
      Kleisli[F, (A, C), (B, C)] {
        case (a, c) => F.map(f.run(a))(b => (b, c))
      }


    def second[A, B, C](f: Kleisli[F, A, B]): Kleisli[F, (C, A), (C, B)] =
      Kleisli[F, (C, A), (C, B)] {
        case (c, a) => F.map(f.run(a))(b => (c, b))
      }

    def mapfst[A, B, C](fab: Kleisli[F, A, B])(f: C => A): Kleisli[F, C, B] = fab local f
    def mapsnd[A, B, C](fab: Kleisli[F, A, B])(f: B => C): Kleisli[F, A, C] = fab map f
  }

}
