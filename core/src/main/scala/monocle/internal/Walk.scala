package monocle.internal

import scalaz.std.function._
import scalaz.{Applicative, Kleisli, Profunctor, \/}

trait Walk[P[_,_]] extends Step[P] {
  def pureP[A]: P[A, A]
  def apP[A, B, C](pab: P[A, B])(f: P[A, B => C]): P[A, C]
}



object Walk {
  def apply[P[_, _]](implicit ev: Walk[P]): Walk[P] = ev

  implicit val function1Walk = new Walk[Function1] {
    def pureP[A]: A => A = identity
    def apP[A, B, C](pab: A => B)(f: A => B => C): A => C = a => f(a)(pab(a))

    def mapfst[A, B, C](pab: A => B)(f: C => A) = Profunctor[Function1].mapfst(pab)(f)
    def mapsnd[A, B, C](pab: A => B)(f: B => C) = Profunctor[Function1].mapsnd(pab)(f)

    def first[A, B, C](pab: A => B)  = Strong[Function1].first(pab)
    def second[A, B, C](pab: A => B) = Strong[Function1].second(pab)

    def left[A, B, C](pab: A => B)  = ProChoice[Function1].left(pab)
    def right[A, B, C](pab: A => B) = ProChoice[Function1].right(pab)
  }

  implicit def kleisliWalk[F[_]: Applicative] = new Walk[Kleisli[F, ?, ?]] {
    def pureP[A]: Kleisli[F, A, A] = Kleisli[F, A, A](Applicative[F].point(_))
    def apP[A, B, C](pab: Kleisli[F, A, B])(f: Kleisli[F, A, B => C]): Kleisli[F, A, C] =
      Kleisli[F, A, C](a =>
        Applicative[F].ap(pab.run(a))(f.run(a))
      )

    def mapfst[A, B, C](pab: Kleisli[F, A, B])(f: C => A): Kleisli[F, C, B] =
      Profunctor[Kleisli[F, ?, ?]].mapfst(pab)(f)
    def mapsnd[A, B, C](pab: Kleisli[F, A, B])(f: B => C): Kleisli[F, A, C] =
      Profunctor[Kleisli[F, ?, ?]].mapsnd(pab)(f)

    def first[A, B, C](pab: Kleisli[F, A, B]): Kleisli[F, (A, C), (B, C)] =
      Strong[Kleisli[F, ?, ?]].first(pab)
    def second[A, B, C](pab: Kleisli[F, A, B]): Kleisli[F, (C, A), (C, B)] =
      Strong[Kleisli[F, ?, ?]].second(pab)

    def left[A, B, C](pab: Kleisli[F, A, B]): Kleisli[F, A \/ C, B \/ C] =
      ProChoice[Kleisli[F, ?, ?]].left(pab)
    def right[A, B, C](pab: Kleisli[F, A, B]): Kleisli[F, C \/ A, C \/ B] =
      ProChoice[Kleisli[F, ?, ?]].right(pab)
  }


}
