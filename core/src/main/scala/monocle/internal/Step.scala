package monocle.internal

import scalaz.std.function._
import scalaz.{Applicative, Kleisli, Profunctor, \/}

trait Step[P[_, _]] extends ProChoice[P] with Strong[P] {
  def step[A, B, C, D](pab: P[A, B]): P[C \/ (A, D), C \/ (B, D)] =
    right(first(pab))
}

object Step {
  def apply[P[_, _]](implicit ev: Step[P]): Step[P] = ev

  implicit def function1Step = new Step[Function1] {
    def mapfst[A, B, C](pab: A => B)(f: C => A) = Profunctor[Function1].mapfst(pab)(f)
    def mapsnd[A, B, C](pab: A => B)(f: B => C) = Profunctor[Function1].mapsnd(pab)(f)

    def first[A, B, C](pab: A => B)  = Strong[Function1].first(pab)
    def second[A, B, C](pab: A => B) = Strong[Function1].second(pab)

    def left[A, B, C](pab: A => B)  = ProChoice[Function1].left(pab)
    def right[A, B, C](pab: A => B) = ProChoice[Function1].right(pab)
  }

  implicit def kleisliStep[F[_]: Applicative] = new Step[Kleisli[F, ?, ?]] {
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
